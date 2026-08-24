package com.example.st2i.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AffectationRequest {
    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotNull(message = "Le profil est obligatoire")
    private Long profilId;
}
