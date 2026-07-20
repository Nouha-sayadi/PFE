package com.example.st2i.Repositories;

import com.example.st2i.Entities.Pointage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    List<Pointage> findByProjetIdAndRessourceIdOrderByMois(Long projetId, Long ressourceId);
    List<Pointage> findByProjetIdOrderByMois(Long projetId);
    Optional<Pointage> findByProjetIdAndRessourceIdAndMois(Long projetId, Long ressourceId, LocalDate mois);
    List<Pointage> findByRessourceIdOrderByMois(Long ressourceId);
    List<Pointage> findByProjetIdAndMois(Long projetId, LocalDate mois);
    List<Pointage> findByProjetId(Long projetId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);

    @Query("SELECT p FROM Pointage p WHERE p.projet.id = :projetId AND p.mois <= :mois ORDER BY p.mois")
    List<Pointage> findByProjetIdAndMoisLessThanEqual(@Param("projetId") Long projetId, @Param("mois") LocalDate mois);

    @Query("SELECT p FROM Pointage p WHERE p.projet.id = :projetId " +
            "AND p.mois >= :debut AND p.mois <= :fin")
    List<Pointage> findByProjetIdAndMoisBetween(
            @Param("projetId") Long projetId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );
}