package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstimationResponse {

    private Long id;
    private Double nbrJours;
    private Double tarifHJ;
    private Double coutEstime;
    private ProjetSummary projet;
    private RessourceSummary ressource;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjetSummary {
        private Long id;
        private String titre;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RessourceSummary {
        private Long id;
        private String nom;
        private String prenom;
    }
}