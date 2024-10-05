package com.example.demo.HistoriaClinica.domain;

import com.example.demo.HistoriaClinica.repository.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    // Obtener todas las historias clínicas
    public List<HistoriaClinica> getAllHistoriasClinicas() {
        return historiaClinicaRepository.findAll();
    }

    // Obtener una historia clínica por DNI de paciente
    public Optional<HistoriaClinica> getHistoriaClinicaByDniPaciente(String dniPaciente) {
        return historiaClinicaRepository.findById(dniPaciente);
    }

    // Guardar una nueva historia clínica
    public HistoriaClinica saveHistoriaClinica(HistoriaClinica historiaClinica) {
        return historiaClinicaRepository.save(historiaClinica);
    }

    // Eliminar una historia clínica
    public void deleteHistoriaClinica(String dniPaciente) {
        historiaClinicaRepository.deleteById(dniPaciente);
    }
}

