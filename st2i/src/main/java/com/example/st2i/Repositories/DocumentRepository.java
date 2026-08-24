package com.example.st2i.Repositories;

import com.example.st2i.Entities.Document;
import com.example.st2i.enums.TypeEntiteDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEntityTypeAndEntityIdOrderByDateUploadDesc(TypeEntiteDocument entityType, Long entityId);
}
