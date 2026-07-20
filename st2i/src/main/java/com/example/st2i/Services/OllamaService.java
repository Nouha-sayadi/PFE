package com.example.st2i.Services;

import com.example.st2i.DTO.EmbeddingRequest;
import com.example.st2i.DTO.EmbeddingResponse;
import com.example.st2i.DTO.OllamaRequest;
import com.example.st2i.DTO.OllamaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OllamaService {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate rest = new RestTemplate();

    /** Envoie un prompt à Ollama et renvoie le texte généré. */
    public String generate(String prompt) {
        OllamaRequest body = new OllamaRequest(model, prompt);
        OllamaResponse res = rest.postForObject(
                baseUrl + "/api/generate", body, OllamaResponse.class);
        return (res != null && res.response != null) ? res.response.trim() : "";
    }

    @Value("${ollama.embed-model}")
    private String embedModel;

    /** Transforme un texte en vecteur (embedding) via nomic-embed-text. */
    public double[] embed(String text) {
        EmbeddingRequest body = new EmbeddingRequest(embedModel, text);
        EmbeddingResponse res = rest.postForObject(
                baseUrl + "/api/embeddings", body, EmbeddingResponse.class);
        if (res == null || res.embedding == null) return new double[0];
        double[] v = new double[res.embedding.size()];
        for (int i = 0; i < v.length; i++) v[i] = res.embedding.get(i);
        return v;
    }

    /** Similarité cosinus entre deux vecteurs (1 = identiques, 0 = sans rapport). */
    public static double cosine(double[] a, double[] b) {
        if (a.length == 0 || b.length == 0 || a.length != b.length) return 0;
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na  += a[i] * a[i];
            nb  += b[i] * b[i];
        }
        return (na == 0 || nb == 0) ? 0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}