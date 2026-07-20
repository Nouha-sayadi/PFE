package com.example.st2i.Repositories;

import com.example.st2i.Entities.CoutReel;
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
public interface CoutReelRepository extends JpaRepository<CoutReel, Long> {

    List<CoutReel> findByProjetId(Long projetId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);

    List<CoutReel> findByProjetIdOrderByMois(Long projetId);

    List<CoutReel> findByProjetIdAndRessourceIdOrderByMois(Long projetId, Long ressourceId);

    Optional<CoutReel> findByProjetIdAndRessourceIdAndMois(Long projetId, Long ressourceId, LocalDate mois);

    @Query("SELECT SUM(c.coutReel) FROM CoutReel c WHERE c.projet.id = :projetId")
    Double getTotalCoutReelByProjet(Long projetId);

    List<CoutReel> findByProjetIdAndMois(Long projetId, LocalDate mois);

    @Query("SELECT c FROM CoutReel c WHERE c.projet.id = :projetId AND c.mois <= :mois ORDER BY c.mois")
    List<CoutReel> findByProjetIdAndMoisLessThanEqual(@Param("projetId") Long projetId, @Param("mois") LocalDate mois);

    @Query("SELECT c FROM CoutReel c WHERE c.projet.id = :projetId " +
            "AND c.mois >= :debut AND c.mois <= :fin")
    List<CoutReel> findByProjetIdAndMoisBetween(
            @Param("projetId") Long projetId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );
}