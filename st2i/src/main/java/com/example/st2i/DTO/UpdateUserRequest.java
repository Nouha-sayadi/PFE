package com.example.st2i.DTO;
import lombok.Data;
import java.util.List;

@Data
public class UpdateUserRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String pays;
    private String codePostal;
    private List<Long> profilIds;
}
