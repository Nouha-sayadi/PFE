package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LivrableRequest {
    private Long projetId;
    private Integer numero;
    private String designation;
    private String phase;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private Boolean realise;
    private Boolean livre;
    private String commentaire;
}