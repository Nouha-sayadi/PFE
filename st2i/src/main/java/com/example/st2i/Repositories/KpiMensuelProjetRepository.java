package com.example.st2i.Repositories;

import com.example.st2i.Entities.KpiMensuelProjet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KpiMensuelProjetRepository extends JpaRepository<KpiMensuelProjet, Long> {
    List<KpiMensuelProjet> findByProjetIdOrderByMois(Long projetId);
    Optional<KpiMensuelProjet> findByProjetIdAndMois(Long projetId, LocalDate mois);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);
    Optional<KpiMensuelProjet> findFirstByProjet_IdOrderByMoisDesc(Long projetId);
    List<KpiMensuelProjet> findTop5ByOrderByIdDesc();

}