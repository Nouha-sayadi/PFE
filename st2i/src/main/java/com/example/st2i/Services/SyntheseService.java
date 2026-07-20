package com.example.st2i.Services;

import com.example.st2i.Entities.KpiMensuelProjet;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.KpiMensuelProjetRepository;
import com.example.st2i.Repositories.ProjetRepository;
import org.springframework.stereotype.Service;

@Service
public class SyntheseService {

    private final OllamaService ollama;
    private final ProjetRepository projetRepo;
    private final KpiMensuelProjetRepository kpiRepo;

    public SyntheseService(OllamaService ollama,
                           ProjetRepository projetRepo,
                           KpiMensuelProjetRepository kpiRepo) {
        this.ollama = ollama;
        this.projetRepo = projetRepo;
        this.kpiRepo = kpiRepo;
    }

    public String genererSynthese(Long projetId) {
        Projet projet = projetRepo.findById(projetId).orElse(null);
        if (projet == null) return "Projet introuvable.";

        KpiMensuelProjet kpi =
                kpiRepo.findFirstByProjet_IdOrderByMoisDesc(projetId).orElse(null);

        StringBuilder data = new StringBuilder();
        data.append("Titre du projet : ").append(projet.getTitre()).append("\n");
        data.append("Budget initial : ").append(projet.getBudgetInitial()).append(" DT\n");
        data.append("Statut : ").append(projet.getStatut()).append("\n");

        if (kpi != null) {
            data.append("Mois : ").append(kpi.getMois()).append("\n");
            data.append("CA production cumulé : ").append(kpi.getCaProduction()).append(" DT\n");
            data.append("CA facturé : ").append(kpi.getCaFacture()).append(" DT\n");
            data.append("Coût réel cumulé : ").append(kpi.getCoutReelCumul()).append(" DT\n");
            data.append("Coût final estimé (EAC) : ").append(kpi.getCoutEAC()).append(" DT\n");
            data.append("Valeur acquise (EV) : ").append(kpi.getEarnedValue()).append("\n");
            data.append("Marge nette actuelle : ").append(kpi.getMargeNetteActuelle()).append("\n");
            data.append("Marge nette EAC (%) : ").append(kpi.getMargeNetteEACPct()).append("\n");
            data.append("Reste à faire (EAC) : ").append(kpi.getResteAFaireEAC()).append(" HJ\n");
        } else {
            data.append("Aucune donnée de suivi mensuel disponible.\n");
        }

        String prompt = """
            Tu es un assistant de gestion de projet. À partir des données ci-dessous,
            rédige une synthèse professionnelle en français (6 à 8 phrases), structurée ainsi :
            1. État général du projet.
            2. Situation financière (budget, coût, rentabilité / marge).
            3. Points d'alerte éventuels (risque de dépassement budgétaire).
            4. Recommandations.
            Base-toi uniquement sur les données fournies, n'invente aucun chiffre.

            Données du projet :
            %s
            """.formatted(data.toString());

        return ollama.generate(prompt);
    }
}