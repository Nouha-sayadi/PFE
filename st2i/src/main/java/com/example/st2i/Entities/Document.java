package com.example.st2i.Entities;

import com.example.st2i.enums.TypeEntiteDocument;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;
    private String nomStocke;
    private String cheminFichier;
    private String typeMime;
    private Long taille;
    private LocalDateTime dateUpload;

    @Enumerated(EnumType.STRING)
    private TypeEntiteDocument entityType;

    private Long entityId;
}
