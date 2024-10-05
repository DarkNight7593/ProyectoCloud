package com.example.demo.Cita.domain;

import com.example.demo.Cita.repository.CitaRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    CitaRepository citaRepository;

    @Autowired
    RestTemplate restTemplate;

    // Obtener todas las citas
    public List<Cita> getAllCitas() {
        return citaRepository.findAll();
    }

    // Obtener una cita por ID
    public Optional<Cita> getCitaById(Long id) {
        return citaRepository.findById(id);
    }

    // URL de la otra API
    private static final String DOCTOR_API_URL = "http://localhost:3000/doctors"; // Cambia esto si la otra API está en otro servidor

    // Guardar una nueva cita y actualizar el total de citas del doctor
    public Cita saveCita(Cita cita) {
        String doctorUrl = DOCTOR_API_URL + "/" + cita.getDniDoctor();
        try {
            // Realizar una solicitud GET para obtener la información del doctor
            ResponseEntity<Map<String, Object>> response = restTemplate.getForEntity(doctorUrl, (Class<Map<String, Object>>)(Class<?>)Map.class);
            Map<String, Object> doctorInfo = response.getBody();

            // Extraer la especialidad del JSON
            if (doctorInfo != null && doctorInfo.containsKey("especialidad")) {
                cita.setEspecialidad((String) doctorInfo.get("especialidad"));
            } else {
                throw new Exception("Especialidad del doctor no encontrada");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la especialidad del doctor: " + e.getMessage());
            throw new RuntimeException("Error al obtener la especialidad del doctor");
        }

        // Guardar la cita en la base de datos
        Cita savedCita = citaRepository.save(cita);

        // Actualizar el total de citas del doctor
        String updateUrl = DOCTOR_API_URL + "/" + cita.getDniDoctor() + "/citas";
        try {
            restTemplate.put(updateUrl, null);
            System.out.println("Total de citas actualizado con éxito");
        } catch (Exception e) {
            System.out.println("Error al actualizar el total de citas: " + e.getMessage());
        }

        return savedCita;
    }


    // Eliminar una cita
    public void deleteCita(Long id) {
        citaRepository.deleteById(id);
    }
}
