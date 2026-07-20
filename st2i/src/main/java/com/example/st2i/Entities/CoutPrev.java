package com.example.st2i.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class CoutPrev {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double nbrJours;
    private LocalDate mois;
    private LocalDate dateDemarrageEffective;
    private LocalDate dateFinPrevu;
    private Double chargePrevuM;
    private Double coutPrev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressource_id")
    private User ressource;
}
