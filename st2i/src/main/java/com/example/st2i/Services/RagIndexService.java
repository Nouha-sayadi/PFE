package com.example.st2i.Services;


import com.example.st2i.Entities.KpiMensuelProjet;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Entities.ProjetDocument;
import com.example.st2i.Repositories.KpiMensuelProjetRepository;
import com.example.st2i.Repositories.ProjetRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagIndexService {

    private final OllamaService ollama;
    private final ProjetRepository projetRepo;
    private final KpiMensuelProjetRepository kpiRepo;

    // L'index en mémoire : un document + son vecteur par projet
    private final List<ProjetDocument> index = new ArrayList<>();

    public RagIndexService(OllamaService ollama,
                           ProjetRepository projetRepo,
                           KpiMensuelProjetRepository kpiRepo) {
        this.ollama = ollama;
        this.projetRepo = projetRepo;
        this.kpiRepo = kpiRepo;
    }

    /** Construit le texte résumé d'un projet (ce que l'IA "lira"). */
    private String buildDocument(Projet p) {
        StringBuilder sb = new StringBuilder();
        sb.append("Projet : ").append(p.getTitre()).append(". ");
        sb.append("Statut : ").append(p.getStatut()).append(". ");
        if (p.getBudgetInitial() != null)
            sb.append("Budget initial : ").append(p.getBudgetInitial()).append(" DT. ");

        kpiRepo.findFirstByProjet_IdOrderByMoisDesc(p.getId()).ifPresent(k -> {
            sb.append("CA production : ").append(k.getCaProduction()).append(" DT. ");
            sb.append("Coût réel cumulé : ").append(k.getCoutReelCumul()).append(" DT. ");
            sb.append("Coût final estimé (EAC) : ").append(k.getCoutEAC()).append(" DT. ");
            sb.append("Marge nette actuelle : ").append(k.getMargeNetteActuelle()).append(" DT. ");
            sb.append("Marge EAC % : ").append(k.getMargeNetteEACPct()).append(". ");
        });
        return sb.toString();
    }

    /** Au démarrage : indexe tous les projets. */
    @PostConstruct
    public void buildIndex() {
        reindex();
    }

    /** (Re)construit l'index complet. */
    public void reindex() {
        index.clear();
        List<Projet> projets = projetRepo.findAll();
        for (Projet p : projets) {
            String doc = buildDocument(p);
            double[] vec = ollama.embed(doc);
            index.add(new ProjetDocument(p.getId(), p.getTitre(), doc, vec));
        }
        System.out.println("✅ RAG : " + index.size() + " projets indexés.");
    }

    /** Recherche les top-K projets les plus proches d'une question. */
    public List<ProjetDocument> search(String question, int topK) {
        double[] qVec = ollama.embed(question);
        return index.stream()
                .sorted((a, b) -> Double.compare(
                        OllamaService.cosine(qVec, b.vector),
                        OllamaService.cosine(qVec, a.vector)))
                .limit(topK)
                .toList();
    }
}