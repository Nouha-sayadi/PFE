package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CoutPrevRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotNull(message = "La ressource est obligatoire")
    private Long ressourceId;

    @NotBlank(message = "Le mois est obligatoire")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Le mois doit être au format yyyy-MM")
    private String mois;

    @NotNull(message = "La charge prévue est obligatoire")
    @PositiveOrZero(message = "La charge prévue doit être positive ou nulle")
    private Double chargePrevuM;

    private Double nbrJours;
}