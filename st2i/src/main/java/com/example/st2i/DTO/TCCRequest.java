package com.example.st2i.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TCCRequest {
    private Long utilisateurId;
    private LocalDate annee;
    private Double tccBase;
    private Double tccAvecFG;
}