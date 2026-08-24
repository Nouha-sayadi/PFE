package com.example.st2i.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RisqueRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    @Min(value = 1, message = "La probabilité doit être comprise entre 1 et 5")
    @Max(value = 5, message = "La probabilité doit être comprise entre 1 et 5")
    private Integer probabilite;

    @Min(value = 1, message = "L'impact doit être compris entre 1 et 5")
    @Max(value = 5, message = "L'impact doit être compris entre 1 et 5")
    private Integer impact;

    private String statut;
    private String mesuresMitigation;
    private LocalDate dateIdentification;
    private LocalDate dateEcheance;
}