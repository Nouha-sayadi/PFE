package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LivrableRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotNull(message = "Le numéro est obligatoire")
    private Integer numero;

    @NotBlank(message = "La désignation est obligatoire")
    private String designation;

    @NotBlank(message = "La phase est obligatoire")
    private String phase;

    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private Boolean realise;
    private Boolean livre;
    private String commentaire;
}