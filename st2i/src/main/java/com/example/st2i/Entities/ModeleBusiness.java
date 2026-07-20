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
public class ModeleBusiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String libelle;
    private String typeGroupement;

    private Boolean partenaireObligatoire = false;

    @OneToMany(mappedBy = "modeleBusiness")
    @JsonIgnore
    private List<Projet> projets;
}
