package com.example.st2i.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContratExtractionResult {
    private ContratExtractDTO contrat = new ContratExtractDTO();
    private List<LivrableExtractDTO> livrables = new ArrayList<>();
    /** Présent uniquement si un projetId a été fourni et qu'au moins un champ du Projet est vide et complétable. */
    private ProjetSuggestionDTO projetSuggestions;
}
