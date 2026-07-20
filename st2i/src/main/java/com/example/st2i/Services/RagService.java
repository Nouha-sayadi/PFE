package com.example.st2i.Services;

import com.example.st2i.Entities.ProjetDocument;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RagService {

    private final RagIndexService ragIndex;
    private final OllamaService ollama;

    public RagService(RagIndexService ragIndex, OllamaService ollama) {
        this.ragIndex = ragIndex;
        this.ollama = ollama;
    }

    public String chat(String question) {
        // 1. RECHERCHE : les 3 projets les plus pertinents
        List<ProjetDocument> pertinents = ragIndex.search(question, 3);

        if (pertinents.isEmpty()) {
            return "Aucune donnée de projet disponible pour répondre.";
        }

        // 2. On assemble le contexte (les documents trouvés)
        StringBuilder contexte = new StringBuilder();
        for (ProjetDocument d : pertinents) {
            contexte.append("- ").append(d.texte).append("\n");
        }

        // 3. GÉNÉRATION : on donne le contexte + la question au modèle
        String prompt = """
            Tu es un assistant de gestion de projet pour la société ST2I.
            Réponds à la question en français, de manière claire et concise.
            Base-toi UNIQUEMENT sur les données de projets fournies ci-dessous.
            Si l'information n'y est pas, dis-le clairement. N'invente aucun chiffre.

            Données des projets pertinents :
            %s
            Question : %s
            """.formatted(contexte.toString(), question);

        return ollama.generate(prompt);
    }
}