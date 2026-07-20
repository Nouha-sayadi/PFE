package com.example.st2i.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private List<String> profilCodes;
}