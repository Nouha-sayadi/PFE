package com.example.st2i.DTO;

import lombok.Data;

@Data
public class AffectationRequest {
    private Long utilisateurId;
    private Long projetId;
    private Long profilId;
}
