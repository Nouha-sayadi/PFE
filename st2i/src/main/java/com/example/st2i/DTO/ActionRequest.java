package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ActionRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    private Long risqueId;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String typeAction;
    private String responsable;
    private LocalDate datePrevue;
    private LocalDate dateReelle;
    private String statut;
    private String commentaire;
}