package com.example.st2i.Repositories;

import com.example.st2i.Entities.Avenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface AvenantRepository extends JpaRepository<Avenant, Long> {
    List<Avenant> findByProjetIdOrderByDateSignatureDesc(Long projetId);
    List<Avenant> findByContratId(Long contratId);

    @Modifying @Transactional
    void deleteByProjetId(Long projetId);
}