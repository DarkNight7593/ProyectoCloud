package com.example.demo.Cita.domain;

import com.example.demo.Diagnostico.domain.Diagnostico;
import com.example.demo.HistoriaClinica.domain.HistoriaClinica;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Getter
@Setter
@Entity
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fecha;
    private Time hora;
    private String especialidad;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "dni_paciente")
    private HistoriaClinica historiaClinica;

    @OneToMany(mappedBy = "cita")
    private List<Diagnostico> diagnosticos;

    // Getters y Setters
}
