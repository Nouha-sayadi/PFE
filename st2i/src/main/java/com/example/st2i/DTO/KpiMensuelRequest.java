package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class KpiMensuelRequest {
    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotBlank(message = "Le mois est obligatoire")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Le mois doit être au format yyyy-MM")
    private String mois;
    private Double earnedValue;
    private Double delivery;
    private Double consommationDelais;
    private Double tauxFacturation;
    private Double chargePrevueMois;
    private Double chargeConsommeeMois;
    private Double chargeConsommeeCumul;
    private Double resteAFaireETC;
    private Double resteAFaireEAC;
    private Double derive;
    private Double coutReel;
    private Double coutReelCumul;
    private Double coutETC;
    private Double coutEAC;
    private Double caProduction;
    private Double caFacture;
    private Double stock;
    private Double margeNetteMois;
    private Double margeNetteActuelle;
    private Double margeNetteEAC;
    private Double margeNetteEACPct;
    private String faitsMarquants;
}
