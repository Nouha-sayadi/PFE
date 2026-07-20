package com.example.st2i.DTO;

import lombok.Data;

@Data
public class PointageRequest {
    private Long projetId;
    private String mois;       // "2024-01"
    private Double nbrJoursReel;
    private String commentaire;
}