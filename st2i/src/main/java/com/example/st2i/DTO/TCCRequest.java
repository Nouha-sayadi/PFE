package com.example.st2i.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TCCRequest {
    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "L'année est obligatoire")
    private LocalDate annee;

    @NotNull(message = "Le TCC de base est obligatoire")
    @PositiveOrZero(message = "Le TCC de base doit être positif ou nul")
    private Double tccBase;

    @NotNull(message = "Le TCC avec FG est obligatoire")
    @PositiveOrZero(message = "Le TCC avec FG doit être positif ou nul")
    private Double tccAvecFG;
}