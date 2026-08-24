package com.example.st2i.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;


@Data
public class ProfilCreateRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    private List<Long> permissionIds;
}
