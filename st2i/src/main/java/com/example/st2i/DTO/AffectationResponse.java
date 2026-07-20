package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffectationResponse {

    private Long id;
    private String dateAffectation;
    private UserSummary utilisateur;
    private ProjetSummary projet;
    private ProfilSummary profil;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String nom;
        private String prenom;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjetSummary {
        private Long id;
        private String titre;
        private String statut;
        private Double tauxAvancement;
        private Double budgetInitial;
        private String dateDemarrage;
        private String dateFinPrevu;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilSummary {
        private Long id;
        private String code;
        private String nom;
    }
}