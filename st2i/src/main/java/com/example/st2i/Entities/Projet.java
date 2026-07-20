package com.example.st2i.Entities;

import com.example.st2i.enums.StatutProjet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;

    private LocalDate dateDemarrageEffective;
    private LocalDate dateDemarrage;
    private LocalDate dateFinReel;
    private LocalDate dateFinPrevu;

    private Double tauxAvancement;
    private Double budgetInitial;
    private Double montantTotal;
    private Double margeNette;

    @Enumerated(EnumType.STRING)
    private StatutProjet statut;

    @ManyToOne
    private Client client;

    @ManyToOne
    private ModeleBusiness modeleBusiness;

    @ManyToOne
    private Devise devise;

    @ManyToOne
    private Bailleur bailleur;

    @ManyToOne
    private Partenaire partenaire;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<Estimation> estimations;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<CoutReel> coutReels;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<CoutPrev> coutPrevisionnels;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<Contrat> contrats;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<KpiMensuelProjet> kpisMensuels;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<EcheanceFacturation> echeances;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<Livrable> livrables;
}