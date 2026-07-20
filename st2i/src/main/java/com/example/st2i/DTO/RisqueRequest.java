package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RisqueRequest {
    private Long projetId;
    private String code;
    private String description;
    private String categorie;
    private Integer probabilite;
    private Integer impact;
    private String statut;
    private String mesuresMitigation;
    private LocalDate dateIdentification;
    private LocalDate dateEcheance;
}