package com.example.st2i.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String raisonSociale;
    private String adresse;
    private String telephone;
    private String email;
    private String pays;

    @DecimalMin(value = "0", message = "La part de groupement doit être comprise entre 0 et 100")
    @DecimalMax(value = "100", message = "La part de groupement doit être comprise entre 0 et 100")
    private Double partGroupement;

    @OneToMany(mappedBy = "partenaire")
    @JsonIgnore
    private List<Projet> projets;
}
