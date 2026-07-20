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

public class Profil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @Column(unique = true)
    private String code;

    @ManyToMany(mappedBy = "profils")
    @JsonIgnore
    private List<User> utilisateurs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "profil_permission",
            joinColumns = @JoinColumn(name = "profil_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;

}
