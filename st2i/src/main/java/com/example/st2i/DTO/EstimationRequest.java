package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EstimationRequest {
    private Long projetId;
    private Long ressourceId;
    private Double nbrJours;
    private LocalDate dateDemarrageEffective;
    private LocalDate dateFinPrevu;
    private Double tarifHJ;
}
