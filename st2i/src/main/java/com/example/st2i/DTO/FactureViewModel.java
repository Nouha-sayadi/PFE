package com.example.st2i.DTO;

import lombok.Builder;
import lombok.Data;

/** Modèle d'affichage pour le template de facture PDF — champs déjà formatés pour éviter la logique dans le template. */
@Data
@Builder
public class FactureViewModel {
    private String numeroFacture;
    private String dateGeneration;

    private String clientNom;
    private String clientAdresse;
    private String clientMatriculeFiscale;

    /** Ligne "N° contrat — intitulé projet", déjà composée (gère les cas où l'un des deux est absent). */
    private String numeroContrat;

    private String objet;
    private String pourcentage;
    private String montantPrevu;
    private String montantFacture;

    private String dateInitiale;
    private String datePrevue;
    private String dateReelle;
    private String statutEmise;

    private String conditionsPaiement;
}
