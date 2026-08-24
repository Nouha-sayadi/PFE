package com.example.st2i.Services;

import com.example.st2i.DTO.ContratExtractionResult;
import com.example.st2i.DTO.ProjetSuggestionDTO;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Exception.AiExtractionException;
import com.example.st2i.Repositories.ProjetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Orchestration OCR (OcrService) + structuration IA (OllamaService) pour le pré-remplissage du formulaire Contrat. */
@Service
public class ContratExtractionService {

    private static final Pattern JSON_BLOCK = Pattern.compile("\\{.*}", Pattern.DOTALL);
    private static final int MAX_TEXT_LENGTH = 12000;

    private final OcrService ocrService;
    private final OllamaService ollamaService;
    private final ProjetRepository projetRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContratExtractionService(OcrService ocrService, OllamaService ollamaService, ProjetRepository projetRepository) {
        this.ocrService = ocrService;
        this.ollamaService = ollamaService;
        this.projetRepository = projetRepository;
    }

    public ContratExtractionResult extract(MultipartFile file, Long projetId) {
        String text = ocrService.extractText(file);
        if (text == null || text.isBlank()) {
            throw new AiExtractionException("Aucun texte n'a pu être extrait du document.");
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            text = text.substring(0, MAX_TEXT_LENGTH);
        }

        String response;
        try {
            response = ollamaService.generate(buildPrompt(text));
        } catch (RestClientException e) {
            throw new AiExtractionException(
                    "Le service IA est indisponible ou a mis trop de temps à répondre.", e);
        }

        ContratExtractionResult result = parseResponse(response);
        if (projetId != null) {
            result.setProjetSuggestions(buildProjetSuggestions(projetId, result));
        }
        return result;
    }

    /** Ne propose que les champs du Projet actuellement vides — ne modifie jamais le Projet lui-même. */
    private ProjetSuggestionDTO buildProjetSuggestions(Long projetId, ContratExtractionResult result) {
        Projet projet = projetRepository.findById(projetId).orElse(null);
        if (projet == null) {
            return null;
        }

        ProjetSuggestionDTO suggestion = new ProjetSuggestionDTO();
        if (projet.getDateDemarrage() == null && result.getContrat().getDateSignature() != null) {
            suggestion.setDateDemarrage(result.getContrat().getDateSignature());
        }
        if (projet.getDateFinPrevu() == null && result.getContrat().getDateEcheance() != null) {
            suggestion.setDateFinPrevu(result.getContrat().getDateEcheance());
        }
        if (projet.getBudgetInitial() == null && result.getContrat().getMontantTotal() != null) {
            suggestion.setBudgetInitial(result.getContrat().getMontantTotal());
        }

        return suggestion.isEmpty() ? null : suggestion;
    }

    private String buildPrompt(String contratText) {
        return """
            Tu es un assistant d'extraction de données pour des contrats de prestation informatique.
            On te fournit le texte brut d'un contrat (extrait par OCR, peut contenir des erreurs de reconnaissance).

            Extrait UNIQUEMENT les informations suivantes et réponds STRICTEMENT avec un objet JSON valide,
            sans aucun texte avant ou après, sans balises markdown, au format exact suivant :

            {
              "contrat": {
                "numeroContrat": string ou null,
                "intitule": string ou null,
                "montantTotal": number ou null,
                "dateSignature": "yyyy-MM-dd" ou null,
                "dateEcheance": "yyyy-MM-dd" ou null,
                "conditionsPaiement": string ou null
              },
              "livrables": [
                { "numero": number, "designation": string, "phase": string ou null, "dateLivraisonPrevue": "yyyy-MM-dd" ou null }
              ]
            }

            Règles :
            - N'invente AUCUNE valeur. Si une information n'est pas présente dans le texte, mets null.
            - Les dates doivent être au format ISO yyyy-MM-dd. Si le format source est ambigu, mets null.
            - montantTotal est un nombre (sans devise, sans espace ni séparateur de milliers).
            - "livrables" est une liste vide [] si aucun livrable n'est identifiable dans le texte.
            - Réponds avec le JSON seul, rien d'autre.

            Texte du contrat :
            %s
            """.formatted(contratText);
    }

    private ContratExtractionResult parseResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new AiExtractionException("Le modèle IA n'a renvoyé aucune réponse.");
        }
        Matcher matcher = JSON_BLOCK.matcher(rawResponse);
        if (!matcher.find()) {
            throw new AiExtractionException("La réponse de l'IA ne contient pas de JSON exploitable.");
        }
        try {
            return objectMapper.readValue(matcher.group(), ContratExtractionResult.class);
        } catch (Exception e) {
            throw new AiExtractionException("La réponse de l'IA n'a pas pu être interprétée.", e);
        }
    }
}
