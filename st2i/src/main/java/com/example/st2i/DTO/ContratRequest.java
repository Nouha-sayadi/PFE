package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContratRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotBlank(message = "Le numéro de contrat est obligatoire")
    private String numeroContrat;

    @NotBlank(message = "L'intitulé est obligatoire")
    private String intitule;

    @NotNull(message = "Le montant total est obligatoire")
    @Positive(message = "Le montant total doit être positif")
    private Double montantTotal;

    private LocalDate dateSignature;
    private LocalDate dateEcheance;
    private String conditionsPaiement;
}