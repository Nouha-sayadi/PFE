package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CoutReelRequest {
    private Long projetId;
    private Long ressourceId;
    private Double nbrJoursReel;
    private LocalDate mois;
    private LocalDate dateDemarrage;
    private LocalDate dateFinReel;
    private Double coutReel;
}