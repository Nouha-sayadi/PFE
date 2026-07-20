package com.example.st2i.DTO;

import lombok.Data;

@Data
public class CoutPrevRequest {
    private Long projetId;
    private Long ressourceId;
    private String mois;
    private Double chargePrevuM;
    private Double nbrJours;
}