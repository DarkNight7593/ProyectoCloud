package com.example.demo.Diagnostico.repository;

import com.example.demo.Diagnostico.domain.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
}

