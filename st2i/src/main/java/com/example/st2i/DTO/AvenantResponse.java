package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AvenantResponse {
    private Long id;
    private String numero;
    private String objet;
    private Double montantInitial;
    private Double montantRevise;
    private Double deltaMontant;
    private Integer impactDelais;
    private String dateSignature;
    private String dateEffet;
    private String statut;
    private String commentaire;
    private Long projetId;
    private Long contratId;
}