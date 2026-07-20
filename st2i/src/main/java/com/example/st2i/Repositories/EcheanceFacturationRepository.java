package com.example.st2i.Repositories;

import com.example.st2i.Entities.EcheanceFacturation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EcheanceFacturationRepository extends JpaRepository<EcheanceFacturation, Long> {
    List<EcheanceFacturation> findByProjetIdOrderByNumero(Long projetId);
    List<EcheanceFacturation> findByContratId(Long contratId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);
    @Query("SELECT e FROM EcheanceFacturation e WHERE e.projet.id = :projetId AND e.emise = true AND e.dateReelle <= :mois")
    List<EcheanceFacturation> findByProjetIdAndEmiseTrueAndDateReelleLessThanEqual(
            @Param("projetId") Long projetId,
            @Param("mois") LocalDate mois
    );
}
