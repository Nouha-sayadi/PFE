package com.example.st2i.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Champ d'un livrable tel qu'extrait par l'IA depuis un document — tous optionnels, non validés. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LivrableExtractDTO {
    private Integer numero;
    private String designation;
    private String phase;
    private String dateLivraisonPrevue;
}
