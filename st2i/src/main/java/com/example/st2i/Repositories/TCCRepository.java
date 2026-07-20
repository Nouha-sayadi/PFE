package com.example.st2i.Repositories;

import com.example.st2i.Entities.TCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TCCRepository extends JpaRepository<TCC, Long> {

    List<TCC> findByUtilisateurId(Long userId);

    Optional<TCC> findTopByUtilisateurIdOrderByAnneeDesc(Long userId);

    // Chercher le TCC d'une année précise (ex : 2026), trié du plus récent au plus ancien
    @Query("SELECT t FROM TCC t WHERE t.utilisateur.id = :userId AND YEAR(t.annee) = :year ORDER BY t.annee DESC")
    List<TCC> findByUtilisateurIdAndYear(@Param("userId") Long userId, @Param("year") int year);
}