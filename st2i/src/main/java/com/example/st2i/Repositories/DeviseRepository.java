package com.example.st2i.Repositories;

import com.example.st2i.Entities.Devise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviseRepository extends JpaRepository<Devise, Long> {}