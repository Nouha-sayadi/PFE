package com.example.st2i.RestController;

import com.example.st2i.DTO.RegisterRequest;
import com.example.st2i.DTO.UpdateUserRequest;
import com.example.st2i.DTO.UserPermissionsResponse;
import com.example.st2i.Entities.User;
import com.example.st2i.Services.KeycloakService;
import com.example.st2i.Services.UserService;
import jakarta.validation.Valid;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest request) {

        String keycloakId = keycloakService.createUser(request);

        return userService.save(request, keycloakId);
    }

    @GetMapping("/me/permissions")
    public UserPermissionsResponse getMyPermissions(JwtAuthenticationToken authentication) {
        String email = authentication.getToken().getClaim("email");
        return userService.getMyPermissions(email);
    }

    @GetMapping("/me")
    public User getCurrentUser(JwtAuthenticationToken authentication) {

        String keycloakId = authentication.getToken().getSubject();
        return userService.findByKeycloakId(keycloakId);

    }



    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }



    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}