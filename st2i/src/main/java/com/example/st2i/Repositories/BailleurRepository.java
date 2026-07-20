package com.example.st2i.Repositories;

import com.example.st2i.Entities.Bailleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BailleurRepository extends JpaRepository<Bailleur, Long> {}