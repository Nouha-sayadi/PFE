package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AvenantRequest {
    private Long projetId;
    private Long contratId;
    private String numero;
    private String objet;
    private Double montantRevise;
    private Integer impactDelais;
    private LocalDate dateSignature;
    private LocalDate dateEffet;
    private String statut;
    private String commentaire;
}