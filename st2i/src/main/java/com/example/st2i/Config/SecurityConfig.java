package com.example.st2i.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/profils/**").permitAll()
                        .requestMatchers("/api/permissions/**").permitAll()
                        .requestMatchers("/api/projets/**").permitAll()
                        .requestMatchers("/api/affectations/**").permitAll()
                        .requestMatchers("/api/estimations/**").permitAll()
                        .requestMatchers("/api/cout-reels/**").permitAll()
                        .requestMatchers("/api/cout-prevs/**").permitAll()
                        .requestMatchers("/api/tcc/**").permitAll()
                        .requestMatchers("/api/bailleurs/**").permitAll()
                        .requestMatchers("/api/clients/**").permitAll()
                        .requestMatchers("/api/devises/**").permitAll()
                        .requestMatchers("/api/modeles/**").permitAll()
                        .requestMatchers("/api/partenaires/**").permitAll()
                        .requestMatchers("/api/contrats/**").permitAll()
                        .requestMatchers("/api/echeances/**").permitAll()
                        .requestMatchers("/api/kpis/**").permitAll()
                        .requestMatchers("/api/pointages/**").permitAll()
                        .requestMatchers("/api/livrables/**").permitAll()
                        .requestMatchers("/api/avenants/**").permitAll()
                        .requestMatchers("/api/risques/**").permitAll()
                        .requestMatchers("/api/actions/**").permitAll()
                        .requestMatchers("/api/notifications/**").permitAll()
                        .requestMatchers("/api/documents/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/ia/**").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                );

        return http.build();
    }
}
