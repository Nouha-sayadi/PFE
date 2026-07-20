package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PointageResponse {
    private Long id;
    private String mois;
    private Double nbrJoursReel;
    private Double tarifHJ;
    private Double coutReel;
    private String statut;
    private String commentaire;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RessourceSummary {
        private Long id;
        private String nom;
        private String prenom;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProjetSummary {
        private Long id;
        private String titre;
    }

    private RessourceSummary ressource;
    private ProjetSummary projet;
}