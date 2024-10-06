const express = require('express');
const router = express.Router();
const axios = require('axios');

// URLs de los microservicios
const PACIENTE_SERVICE_URL = 'http://localhost:8082/pacientes';
const DOCTOR_SERVICE_URL = 'http://localhost:3000/doctors';
const DISPONIBILIDAD_SERVICE_URL = 'http://localhost:3000/disponibilidad';
const CITA_SERVICE_URL = 'http://localhost:8080/citas';

// Ruta para agendar una cita
router.post('/agendar', async (req, res) => {
    const { dniPaciente, nombres, apellidos, fechaNacimiento, dniDoctor, fecha, hora, seguro } = req.body;

    try {
        // 1. Verificar si el paciente existe
        let pacienteResponse;
        try {
            pacienteResponse = await axios.get(`${PACIENTE_SERVICE_URL}/${dniPaciente}`);
            console.log(`Paciente con DNI ${dniPaciente} encontrado.`);
        } catch (error) {
            if (error.response && error.response.status === 404) {
                // Si el paciente no existe, crearlo con o sin seguro
                const newPaciente = {
                    _id: dniPaciente,
                    nombres,
                    apellidos,
                    fecha_nacimiento: fechaNacimiento,
                    ...(seguro && { seguro }) // Añadir seguro si existe
                };
                pacienteResponse = await axios.post(PACIENTE_SERVICE_URL, newPaciente);
                console.log(`Paciente con DNI ${dniPaciente} no encontrado. Paciente creado:`, pacienteResponse.data);
            } else {
                console.error('Error al verificar el paciente:', error.message);
                throw new Error('Error al verificar el paciente');
            }
        }

        // 2. Verificar que el doctor existe
        try {
            const doctorResponse = await axios.get(`${DOCTOR_SERVICE_URL}/${dniDoctor}`);

            if (doctorResponse?.data) {
                console.log(`Doctor con DNI ${dniDoctor} encontrado.`);
            } else {
                return res.status(404).send('Doctor no encontrado');
            }
        } catch (error) {
            const statusCode = error.response?.status || 500;
            const errorMessage = statusCode === 404 ? 'Doctor no encontrado' : 'Error al verificar el doctor';
            console.error(errorMessage, error.message);
            return res.status(statusCode).send(errorMessage);
        }



        // 3. Verificar la disponibilidad del doctor
        const disponibilidadResponse = await axios.get(`${DISPONIBILIDAD_SERVICE_URL}/${dniDoctor}`);
        const disponibilidad = disponibilidadResponse.data;

        if (!disponibilidad.some(d => d.dia === fecha && d.hora === hora)) {
            console.error(`El doctor con DNI ${dniDoctor} no está disponible en la fecha ${fecha} y hora ${hora}.`);
            return res.status(400).send('El doctor no está disponible en la fecha y hora solicitadas.');
        }
        console.log(`Doctor disponible en la fecha ${fecha} y hora ${hora}.`);

        // 4. Crear la cita
        const citaData = {
            dniPaciente,
            dniDoctor,
            fecha,
            hora
        };

        const citaResponse = await axios.post(`${CITA_SERVICE_URL}/${dniPaciente}`, citaData);
        console.log(`Cita creada con éxito:`, citaResponse.data);

        res.status(201).send('Cita agendada con éxito');

    } catch (error) {
        console.error('Error al agendar la cita:', error.message);
        res.status(500).send('Error al agendar la cita');
    }
});

// Obtener las citas del paciente con el DNI y los detalles del doctor (sin el contador de citas)
router.get('/:dniPaciente', async (req, res) => {
    const { dniPaciente } = req.params;

    try {
        // 1. Obtener las citas del paciente
        const citasResponse = await axios.get(`${CITA_SERVICE_URL}/paciente/${dniPaciente}`);
        const citas = citasResponse.data;

        if (!citas.length) {
            return res.status(404).send(`No se encontraron citas para el paciente con DNI ${dniPaciente}`);
        }

        // 2. Para cada cita, obtener los detalles del doctor (sin el contador de citas)
        const citasConDoctor = await Promise.all(citas.map(async (cita) => {
            try {
                const doctorResponse = await axios.get(`${DOCTOR_SERVICE_URL}/${cita.dniDoctor}`);
                const doctorData = doctorResponse.data;

                // Agregar los datos del doctor (sin el contador de citas)
                return {
                    fecha: cita.fecha,
                    hora: cita.hora,
                    doctor: {
                        nombres: doctorData.nombres,
                        apellidos: doctorData.apellidos,
                        especialidad: doctorData.especialidad
                    }
                };
            } catch (error) {
                console.error(`Error al obtener los datos del doctor con DNI ${cita.dniDoctor}:`, error.message);
                return { ...cita, doctor: 'Error al obtener los datos del doctor' };
            }
        }));

        // 3. Devolver las citas con la información del doctor
        res.json(citasConDoctor);

    } catch (error) {
        console.error('Error al obtener las citas del paciente:', error.message);
        res.status(500).send('Error al obtener las citas del paciente');
    }
});

module.exports = router;

