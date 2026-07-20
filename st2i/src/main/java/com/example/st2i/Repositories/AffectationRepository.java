package com.example.st2i.Repositories;

import com.example.st2i.Entities.Affectation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {

    List<Affectation> findByProjetId(Long projetId);
    List<Affectation> findByUtilisateurId(Long userId);
    Optional<Affectation> findByUtilisateurIdAndProjetId(Long userId, Long projetId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);
    boolean existsByProjetIdAndProfilCode(Long projetId, String profilCode);
}