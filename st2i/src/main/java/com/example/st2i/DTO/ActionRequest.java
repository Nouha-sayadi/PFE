package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ActionRequest {
    private Long projetId;
    private Long risqueId;
    private String code;
    private String description;
    private String typeAction;
    private String responsable;
    private LocalDate datePrevue;
    private LocalDate dateReelle;
    private String statut;
    private String commentaire;
}