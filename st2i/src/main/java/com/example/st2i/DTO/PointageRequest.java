package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PointageRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotBlank(message = "Le mois est obligatoire")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Le mois doit être au format yyyy-MM")
    private String mois;       // "2024-01"

    @NotNull(message = "Le nombre de jours est obligatoire")
    @PositiveOrZero(message = "Le nombre de jours doit être positif ou nul")
    private Double nbrJoursReel;

    private String commentaire;
}