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
public class Bailleur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private String typeFinancement;
    private String pays;

    @OneToMany(mappedBy = "bailleur")
    @JsonIgnore
    private List<Projet> projets;
}
