package com.example.st2i.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse {
    public List<Double> embedding;
}
