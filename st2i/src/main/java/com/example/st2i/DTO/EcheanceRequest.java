package com.example.st2i.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EcheanceRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    private Long contratId;

    @NotNull(message = "Le numéro est obligatoire")
    private Integer numero;

    @NotBlank(message = "L'objet est obligatoire")
    private String objet;

    @DecimalMin(value = "0", message = "Le pourcentage doit être compris entre 0 et 1")
    @DecimalMax(value = "1", message = "Le pourcentage doit être compris entre 0 et 1")
    private Double pourcentage;

    @PositiveOrZero(message = "Le montant prévu doit être positif ou nul")
    private Double montantPrevu;

    @PositiveOrZero(message = "Le montant facturé doit être positif ou nul")
    private Double montantFacture;

    private LocalDate dateInitiale;
    private LocalDate datePrevActualisee;
    private LocalDate dateReelle;
    private Boolean emise;
    private String commentaire;
}
