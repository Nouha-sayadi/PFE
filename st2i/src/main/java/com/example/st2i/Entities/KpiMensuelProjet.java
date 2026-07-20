package com.example.st2i.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KpiMensuelProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate mois;

    @Builder.Default private Double earnedValue = 0.0;
    @Builder.Default private Double delivery = 0.0;
    @Builder.Default private Double consommationDelais = 0.0;
    @Builder.Default private Double tauxFacturation = 0.0;

    @Builder.Default private Double chargePrevueMois = 0.0;
    @Builder.Default private Double chargeConsommeeMois = 0.0;
    @Builder.Default private Double chargeConsommeeCumul = 0.0;
    @Builder.Default private Double resteAFaireETC = 0.0;
    @Builder.Default private Double resteAFaireEAC = 0.0;
    @Builder.Default private Double derive = 0.0;

    @Builder.Default private Double coutReel = 0.0;
    @Builder.Default private Double coutReelCumul = 0.0;
    @Builder.Default private Double coutETC = 0.0;
    @Builder.Default private Double coutEAC = 0.0;

    @Builder.Default private Double caProduction = 0.0;
    @Builder.Default private Double caFacture = 0.0;
    @Builder.Default private Double stock = 0.0;

    @Builder.Default private Double margeNetteMois = 0.0;
    @Builder.Default private Double margeNetteActuelle = 0.0;
    @Builder.Default private Double margeNetteEAC = 0.0;
    @Builder.Default private Double margeNetteEACPct = 0.0;

    private String faitsMarquants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;
}