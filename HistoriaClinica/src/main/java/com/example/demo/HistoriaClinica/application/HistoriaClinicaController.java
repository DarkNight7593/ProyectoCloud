package com.example.demo.HistoriaClinica.application;

import com.example.demo.HistoriaClinica.domain.HistoriaClinica;
import com.example.demo.HistoriaClinica.domain.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/historias-clinicas")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    // Obtener todas las historias clínicas
    @GetMapping
    public List<HistoriaClinica> getAllHistoriasClinicas() {
        return historiaClinicaService.getAllHistoriasClinicas();
    }

    // Obtener una historia clínica por DNI de paciente
    @GetMapping("/{dniPaciente}")
    public Optional<HistoriaClinica> getHistoriaClinicaByDniPaciente(@PathVariable String dniPaciente) {
        return historiaClinicaService.getHistoriaClinicaByDniPaciente(dniPaciente);
    }

    // Crear una nueva historia clínica
    @PostMapping
    public HistoriaClinica createHistoriaClinica(@RequestBody HistoriaClinica historiaClinica) {
        return historiaClinicaService.saveHistoriaClinica(historiaClinica);
    }


    // Eliminar una historia clínica
    @DeleteMapping("/{dniPaciente}")
    public void deleteHistoriaClinica(@PathVariable String dniPaciente) {
        historiaClinicaService.deleteHistoriaClinica(dniPaciente);
    }
}

