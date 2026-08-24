package com.example.st2i.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EstimationRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotNull(message = "La ressource est obligatoire")
    private Long ressourceId;

    @PositiveOrZero(message = "Le nombre de jours doit être positif ou nul")
    private Double nbrJours;

    private LocalDate dateDemarrageEffective;
    private LocalDate dateFinPrevu;

    @PositiveOrZero(message = "Le tarif journalier doit être positif ou nul")
    private Double tarifHJ;
}
