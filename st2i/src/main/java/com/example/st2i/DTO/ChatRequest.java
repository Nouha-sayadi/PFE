package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "La question est obligatoire")
    private String question;

}