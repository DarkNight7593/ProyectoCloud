package com.example.demo.Diagnostico.domain;

import com.example.demo.Diagnostico.repository.DiagnosticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticoService {

    @Autowired
    private DiagnosticoRepository diagnosticoRepository;

    // Obtener todos los diagnósticos
    public List<Diagnostico> getAllDiagnosticos() {
        return diagnosticoRepository.findAll();
    }

    // Obtener un diagnóstico por ID
    public Optional<Diagnostico> getDiagnosticoById(Long id) {
        return diagnosticoRepository.findById(id);
    }

    // Guardar un nuevo diagnóstico
    public Diagnostico saveDiagnostico(Diagnostico diagnostico) {
        return diagnosticoRepository.save(diagnostico);
    }


    // Eliminar un diagnóstico
    public void deleteDiagnostico(Long id) {
        diagnosticoRepository.deleteById(id);
    }
}

