package com.example.demo.Cita.application;

import com.example.demo.Cita.domain.Cita;
import com.example.demo.Cita.domain.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    // Obtener todas las citas
    @GetMapping
    public List<Cita> getAllCitas() {
        return citaService.getAllCitas();
    }

    // Obtener una cita por ID
    @GetMapping("/{id}")
    public Optional<Cita> getCitaById(@PathVariable Long id) {
        return citaService.getCitaById(id);
    }

    // Crear una nueva cita
    @PostMapping
    public Cita createCita(@RequestBody Cita cita) {
        return citaService.saveCita(cita);
    }

    // Eliminar una cita
    @DeleteMapping("/{id}")
    public void deleteCita(@PathVariable Long id) {
        citaService.deleteCita(id);
    }
}

