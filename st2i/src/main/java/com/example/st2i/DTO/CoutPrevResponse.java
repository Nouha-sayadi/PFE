package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoutPrevResponse {

    private Long id;
    private String mois;
    private Double chargePrevuM;
    private Double coutPrev;
    private Double nbrJours;
    private String dateDemarrageEffective;
    private String dateFinPrevu;

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