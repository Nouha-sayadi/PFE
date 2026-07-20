package com.example.st2i.Repositories;

import com.example.st2i.Entities.Contrat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {
    List<Contrat> findByProjetId(Long projetId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);
}
