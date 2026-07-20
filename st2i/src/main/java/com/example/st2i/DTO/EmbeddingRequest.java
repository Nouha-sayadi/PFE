package com.example.st2i.DTO;

public class EmbeddingRequest {
    public String model;
    public String prompt;
    public EmbeddingRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }
}
