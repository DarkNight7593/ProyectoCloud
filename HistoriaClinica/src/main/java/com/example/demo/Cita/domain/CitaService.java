package com.example.demo.Cita.domain;

import com.example.demo.Cita.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    CitaRepository citaRepository;

    // Obtener todas las citas
    public List<Cita> getAllCitas() {
        return citaRepository.findAll();
    }

    // Obtener una cita por ID
    public Optional<Cita> getCitaById(Long id) {
        return citaRepository.findById(id);
    }

    // Guardar una nueva cita
    public Cita saveCita(Cita cita) {
        return citaRepository.save(cita);
    }

    // Eliminar una cita
    public void deleteCita(Long id) {
        citaRepository.deleteById(id);
    }
}

