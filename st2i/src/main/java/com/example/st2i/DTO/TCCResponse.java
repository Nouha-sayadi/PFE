package com.example.st2i.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCCResponse {

    private Long id;
    private String annee;
    private Double tccBase;
    private Double tccAvecFG;
    private Double tarifHJ;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String nom;
        private String prenom;
    }

    private UserSummary utilisateur;
}