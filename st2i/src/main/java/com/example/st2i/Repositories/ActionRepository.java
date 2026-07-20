package com.example.st2i.Repositories;

import com.example.st2i.Entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> findByProjetIdOrderByDatePrevueDesc(Long projetId);
    List<Action> findByRisqueId(Long risqueId);

    @Modifying @Transactional
    void deleteByProjetId(Long projetId);
}