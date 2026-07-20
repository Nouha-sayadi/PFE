package com.example.st2i.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Livrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;
    private String designation;
    private String phase;

    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;

    private Boolean realise = false;
    private Boolean livre   = false;

    private Double delivery = 0.0;

    private Integer ecartJours;

    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;
}