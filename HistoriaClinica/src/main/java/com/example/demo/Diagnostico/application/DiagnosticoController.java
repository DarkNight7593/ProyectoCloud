package com.example.demo.Diagnostico.application;

import com.example.demo.Diagnostico.domain.Diagnostico;
import com.example.demo.Diagnostico.domain.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diagnosticos")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService diagnosticoService;

    // Obtener todos los diagnósticos
    @GetMapping
    public List<Diagnostico> getAllDiagnosticos() {
        return diagnosticoService.getAllDiagnosticos();
    }

    // Obtener un diagnóstico por ID
    @GetMapping("/{id}")
    public Optional<Diagnostico> getDiagnosticoById(@PathVariable Long id) {
        return diagnosticoService.getDiagnosticoById(id);
    }

    // Crear un nuevo diagnóstico
    @PostMapping
    public Diagnostico createDiagnostico(@RequestBody Diagnostico diagnostico) {
        return diagnosticoService.saveDiagnostico(diagnostico);
    }

    // Eliminar un diagnóstico
    @DeleteMapping("/{id}")
    public void deleteDiagnostico(@PathVariable Long id) {
        diagnosticoService.deleteDiagnostico(id);
    }
}

