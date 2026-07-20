package com.example.st2i.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Partenaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String nom;
    private String raisonSociale;
    private String adresse;
    private String telephone;
    private String email;
    private String pays;
    private Double partGroupement;

    @OneToMany(mappedBy = "partenaire")
    @JsonIgnore
    private List<Projet> projets;
}
