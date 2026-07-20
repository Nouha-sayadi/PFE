package com.example.st2i.Repositories;

import com.example.st2i.Entities.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfilRepository extends JpaRepository<Profil, Long> {
    Optional<Profil> findByCode(String code);
    List<Profil> findByCodeIn(List<String> codes);
}
