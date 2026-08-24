package com.example.st2i.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

public class Client {
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
    private String matriculeFiscale;
    private String registreCommerce;
    private String secteurActivite;
    private String pays;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<Projet> projets;
}
