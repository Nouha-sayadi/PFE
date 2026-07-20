package com.example.st2i.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContratRequest {
    private Long projetId;
    private String numeroContrat;
    private String intitule;
    private Double montantTotal;
    private LocalDate dateSignature;
    private LocalDate dateEcheance;
    private String conditionsPaiement;
}