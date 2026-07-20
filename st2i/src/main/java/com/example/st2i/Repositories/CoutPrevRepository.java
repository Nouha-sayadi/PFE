package com.example.st2i.Repositories;

import com.example.st2i.Entities.CoutPrev;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CoutPrevRepository extends JpaRepository<CoutPrev, Long> {

    List<CoutPrev> findByProjetId(Long projetId);
    List<CoutPrev> findByProjetIdAndRessourceId(Long projetId, Long ressourceId);
    List<CoutPrev> findByProjetIdAndMois(Long projetId, LocalDate mois);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);

    @Query("SELECT c FROM CoutPrev c WHERE c.projet.id = :projetId " +
            "AND c.mois >= :debut AND c.mois <= :fin")
    List<CoutPrev> findByProjetIdAndMoisBetween(
            @Param("projetId") Long projetId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );
}