package com.example.st2i.Repositories;

import com.example.st2i.Entities.Estimation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstimationRepository extends JpaRepository<Estimation, Long> {
    List<Estimation> findByProjetId(Long projetId);
    Optional<Estimation> findByProjetIdAndRessourceId(Long projetId, Long ressourceId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);
}