package com.example.st2i.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Suggestions de complétion du Projet parent, dérivées d'un contrat extrait — uniquement pour les champs actuellement vides. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjetSuggestionDTO {
    private String dateDemarrage;
    private String dateFinPrevu;
    private Double budgetInitial;

    @JsonIgnore
    public boolean isEmpty() {
        return dateDemarrage == null && dateFinPrevu == null && budgetInitial == null;
    }
}
