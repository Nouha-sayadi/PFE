package com.example.st2i.Services;
import com.example.st2i.DTO.RegisterRequest;

import com.example.st2i.Entities.User;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    private final String realm = "st2i-realm";

    public String createUser(RegisterRequest request) {

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(request.getNom());
        user.setLastName(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);

        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        if (response.getStatus() != 201) {

            String error = response.readEntity(String.class);

            System.out.println("Keycloak error status: " + response.getStatus());
            System.out.println("Keycloak error body: " + error);

            response.close();

            throw new RuntimeException("Erreur Keycloak: " + error);
        }

        String userId = response.getLocation().getPath()
                .replaceAll(".*/([^/]+)$", "$1");

        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.getPassword());
        password.setTemporary(false);

        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(password);

        return userId;
    }

    public void deleteUser(String keycloakId) {
        keycloak.realm(realm)
                .users()
                .delete(keycloakId);
    }

    public List<UserRepresentation> getAllUsers() {
        return keycloak.realm(realm)
                .users()
                .list();
    }

    public List<UserRepresentation> searchByEmail(String email) {
        return keycloak.realm(realm)
                .users()
                .search(email);
    }
    public void createUserFromEntity(User user) {

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setEmail(user.getEmail());
        kcUser.setFirstName(user.getNom());
        kcUser.setLastName(user.getPrenom());
        kcUser.setEnabled(true);

        Response response = keycloak.realm(realm)
                .users()
                .create(kcUser);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Erreur création Keycloak");
        }
    }
}