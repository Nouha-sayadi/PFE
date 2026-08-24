package com.example.st2i.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Champs du contrat tels qu'extraits par l'IA depuis un document — tous optionnels, non validés. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContratExtractDTO {
    private String numeroContrat;
    private String intitule;
    private Double montantTotal;
    private String dateSignature;
    private String dateEcheance;
    private String conditionsPaiement;
}
