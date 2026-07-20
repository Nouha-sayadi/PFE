package com.example.st2i.Services;

import com.example.st2i.DTO.RegisterRequest;
import com.example.st2i.DTO.UpdateUserRequest;
import com.example.st2i.Entities.Profil;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.ProfilRepository;
import com.example.st2i.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.st2i.DTO.PermissionDTO;
import com.example.st2i.DTO.UserPermissionsResponse;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilRepository profilRepository;

    public User save(RegisterRequest request, String keycloakId) {

        List<Profil> profils;

        if (request.getProfilCodes() == null || request.getProfilCodes().isEmpty()) {

            Profil profil = profilRepository.findByCode("USER")
                    .orElseThrow(() -> new RuntimeException("Profil USER introuvable"));

            profils = List.of(profil);

        } else {
            profils = profilRepository.findByCodeIn(request.getProfilCodes());

            if (profils.isEmpty()) {
                throw new RuntimeException("Aucun profil valide trouvé");
            }
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(request.getPassword())
                .keycloakId(keycloakId)
                .profils(profils)
                .build();

        return userRepository.save(user);
    }



    public UserPermissionsResponse getMyPermissions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));

        UserPermissionsResponse response = new UserPermissionsResponse();
        response.setEmail(user.getEmail());
        response.setUsername(user.getNom() + " " + user.getPrenom());

        if (user.getProfils() == null || user.getProfils().isEmpty()) {
            response.setProfil(null);
            response.setPermissions(List.of());
            return response;
        }

        response.setProfil(user.getProfils().get(0).getCode());

        List<PermissionDTO> permDTOs = user.getProfils().stream()
                .flatMap(profil -> profil.getPermissions().stream())
                .filter(p -> p.getCode().startsWith("GET_")
                        || p.getCode().startsWith("POST_")
                        || p.getCode().startsWith("PUT_")
                        || p.getCode().startsWith("DELETE_"))
                .distinct()
                .map(p -> {
                    PermissionDTO dto = new PermissionDTO();
                    dto.setId(p.getId());
                    dto.setCode(p.getCode());
                    dto.setLibelle(p.getLibelle());
                    dto.setModule(p.getModule());
                    return dto;
                })
                .collect(Collectors.toList());

        response.setPermissions(permDTOs);
        return response;
    }


    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User non trouvé avec keycloakId: " + keycloakId));
    }




    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User non trouvé avec email: " + email));
    }




    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User non trouvé"));
    }

    public User update(Long id, UpdateUserRequest request) {
        User existing = findById(id);

        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        existing.setEmail(request.getEmail());

        if (request.getTelephone() != null)
            existing.setTelephone(request.getTelephone());
        if (request.getPays() != null)
            existing.setVille(request.getPays());
        if (request.getCodePostal() != null)
            existing.setCodePostal(request.getCodePostal());

        if (request.getProfilIds() != null && !request.getProfilIds().isEmpty()) {
            List<Profil> managedProfils = profilRepository.findAllById(request.getProfilIds());
            existing.setProfils(managedProfils);
        }

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            existing.setMotDePasse(request.getMotDePasse());
        }

        return userRepository.save(existing);
    }



    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}