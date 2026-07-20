package com.example.st2i.Repositories;

import com.example.st2i.Entities.Risque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RisqueRepository extends JpaRepository<Risque, Long> {
    List<Risque> findByProjetIdOrderByDateIdentificationDesc(Long projetId);

    @Modifying @Transactional
    void deleteByProjetId(Long projetId);
    List<Risque> findTop5ByOrderByIdDesc();

}