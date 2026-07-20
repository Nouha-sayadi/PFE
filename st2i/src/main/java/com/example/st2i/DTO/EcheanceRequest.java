package com.example.st2i.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EcheanceRequest {
    private Long projetId;
    private Long contratId;
    private Integer numero;
    private String objet;
    private Double pourcentage;
    private Double montantPrevu;
    private Double montantFacture;
    private LocalDate dateInitiale;
    private LocalDate datePrevActualisee;
    private LocalDate dateReelle;
    private Boolean emise;
    private String commentaire;
}
