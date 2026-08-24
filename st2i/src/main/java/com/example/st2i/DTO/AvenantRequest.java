package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AvenantRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    private Long contratId;

    @NotBlank(message = "Le numéro est obligatoire")
    private String numero;

    @NotBlank(message = "L'objet est obligatoire")
    private String objet;

    @PositiveOrZero(message = "Le montant révisé doit être positif ou nul")
    private Double montantRevise;

    private Integer impactDelais;
    private LocalDate dateSignature;
    private LocalDate dateEffet;
    private String statut;
    private String commentaire;
}