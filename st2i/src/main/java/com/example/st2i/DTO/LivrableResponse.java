package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LivrableResponse {
    private Long id;
    private Integer numero;
    private String designation;
    private String phase;
    private String dateLivraisonPrevue;
    private String dateLivraisonReelle;
    private Boolean realise;
    private Boolean livre;
    private Double delivery;
    private Integer ecartJours;
    private String commentaire;
    private Double deliveryGlobal; // % delivery du projet entier

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProjetSummary {
        private Long id;
        private String titre;
    }
    private ProjetSummary projet;
}