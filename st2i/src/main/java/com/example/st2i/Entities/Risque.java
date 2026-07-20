package com.example.st2i.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Risque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;
    private String categorie;
    private Integer probabilite;
    private Integer impact;
    private Integer criticite;
    private String statut;
    private String mesuresMitigation;
    private LocalDate dateIdentification;
    private LocalDate dateEcheance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @JsonIgnore
    @OneToMany(mappedBy = "risque", cascade = CascadeType.ALL)
    private List<Action> actions;
}