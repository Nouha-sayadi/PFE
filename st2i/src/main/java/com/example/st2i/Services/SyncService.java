package com.example.st2i.Services;

import com.example.st2i.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    public void syncKeycloakWithDatabase() {

        List<UserRepresentation> kcUsers = keycloakService.getAllUsers();

        for (UserRepresentation kcUser : kcUsers) {

            String email = kcUser.getEmail();
            String username = kcUser.getUsername();

            if (username.equals("admin") || username.startsWith("service-account")) {
                continue;
            }

            if (email == null) {
                continue;
            }

            boolean exists = userRepository.findByEmail(email).isPresent();

            if (!exists) {
                keycloakService.deleteUser(kcUser.getId());
                System.out.println("User supprimé de Keycloak: " + email);
            }
        }
    }



    public void syncDatabaseToKeycloak() {

        userRepository.findAll().forEach(user -> {

            List<UserRepresentation> kcUsers =
                    keycloakService.searchByEmail(user.getEmail());

            if (kcUsers.isEmpty()) {
                System.out.println("Création user dans Keycloak: " + user.getEmail());

                keycloakService.createUserFromEntity(user);
            }
        });
    }

    public void fullSync() {
        syncDatabaseToKeycloak();
        syncKeycloakWithDatabase();
    }

    @Scheduled(fixedRate = 60000) // chaque 60 secondes
    public void autoSync() {
        fullSync();
    }
}
