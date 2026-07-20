package com.example.st2i.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroContrat;
    private String intitule;
    private Double montantTotal;
    private LocalDate dateSignature;
    private LocalDate dateEcheance;
    private String conditionsPaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @JsonIgnore
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL)
    private List<EcheanceFacturation> echeances;
}