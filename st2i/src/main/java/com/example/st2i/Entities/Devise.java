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
public class Devise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String libelle;
    private Double tauxChange;
    private Boolean estDeviseBase = false;

    @OneToMany(mappedBy = "devise")
    @JsonIgnore
    private List<Projet> projets;
}
