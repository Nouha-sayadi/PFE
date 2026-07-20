package com.example.st2i.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String keycloakId;

    private String telephone;
    private String ville;
    private String codePostal;


    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TCC> tccs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_profil",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "profil_id")
    )
    private List<Profil> profils;

    @OneToMany(mappedBy = "utilisateur")
    @JsonIgnore
    private List<Affectation> affectations;
}
