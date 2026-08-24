package com.example.st2i.DTO;

import lombok.Data;

import java.time.LocalDate;

/** Complétion optionnelle des champs actuellement vides du Projet. Aucun champ n'est obligatoire. */
@Data
public class ProjetCompleteRequest {
    private LocalDate dateDemarrage;
    private LocalDate dateFinPrevu;
    private Double budgetInitial;
}
