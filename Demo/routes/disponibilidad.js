const express = require('express');
const router = express.Router();
const { Pool } = require('pg');

// Configuración de la conexión a PostgreSQL
const pool = new Pool({
    user: 'postgres',
    host: 'localhost',
    database: 'hospital',
    password: 'postgres',
    port: 5432, // El puerto por defecto de PostgreSQL
});

pool.connect((err, client, release) => {
    if (err) {
        return console.error('Error al conectar a la base de datos:', err.stack);
    }
    console.log('Conexión exitosa con la base de datos PostgreSQL');
    release();
});

// Obtener la disponibilidad de un doctor específico
router.get('/:dni', async (req, res) => {
    const { dni } = req.params;
    try {
        const result = await pool.query(
            'SELECT * FROM Disponibilidad WHERE dni_doctor = $1',
            [dni]
        );
        res.json(result.rows);
    } catch (err) {
        res.status(500).send('Error al obtener la disponibilidad del doctor');
    }
});

// Agregar disponibilidad para un doctor
router.post('/:dni', async (req, res) => {
    const { dni } = req.params;  // Capturamos el dni del doctor desde los parámetros de la URL
    const { dia, hora } = req.body;  // El día y la hora vienen en el cuerpo de la solicitud
    try {
        await pool.query(
            'INSERT INTO Disponibilidad (dia, hora, dni_doctor) VALUES ($1, $2, $3)',
            [dia, hora, dni]
        );
        res.send('Disponibilidad agregada con éxito');
    } catch (err) {
        res.status(500).send('Error al agregar la disponibilidad');
    }
});

module.exports = router;
