package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoutReelResponse {

    private Long id;
    private Double nbrJoursReel;
    private String mois;
    private String dateDemarrage;
    private String dateFinReel;
    private Double coutReel;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProjetSummary {
        private Long id;
        private String titre;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RessourceSummary {
        private Long id;
        private String nom;
        private String prenom;
    }

    private ProjetSummary projet;
    private RessourceSummary ressource;
}