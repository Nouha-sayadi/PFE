package com.example.st2i.Repositories;

import com.example.st2i.Entities.Livrable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LivrableRepository extends JpaRepository<Livrable, Long> {

    List<Livrable> findByProjetIdOrderByNumero(Long projetId);
    @Modifying
    @Transactional
    void deleteByProjetId(Long projetId);
    long countByProjetId(Long projetId);

    long countByProjetIdAndLivreTrue(Long projetId);

    @Query("SELECT AVG(l.delivery) FROM Livrable l WHERE l.projet.id = :projetId")
    Double getDeliveryMoyenByProjet(Long projetId);
}