package com.example.st2i.Services;

import com.example.st2i.DTO.RisqueRequest;
import com.example.st2i.Entities.Risque;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.RisqueRepository;
import com.example.st2i.Repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RisqueService {

    @Autowired private RisqueRepository risqueRepository;
    @Autowired private ProjetRepository projetRepository;

    public Risque create(RisqueRequest req) {
        Projet projet = projetRepository.findById(req.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));


        int criticite = (req.getProbabilite() != null ? req.getProbabilite() : 1)
                * (req.getImpact() != null ? req.getImpact() : 1);

        Risque risque = Risque.builder()
                .projet(projet)
                .code(req.getCode())
                .description(req.getDescription())
                .categorie(req.getCategorie())
                .probabilite(req.getProbabilite())
                .impact(req.getImpact())
                .criticite(criticite)
                .statut(req.getStatut() != null ? req.getStatut() : "IDENTIFIE")
                .mesuresMitigation(req.getMesuresMitigation())
                .dateIdentification(req.getDateIdentification())
                .dateEcheance(req.getDateEcheance())
                .build();

        return risqueRepository.save(risque);
    }

    public Risque update(Long id, RisqueRequest req) {
        Risque risque = risqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Risque non trouvé"));

        int criticite = (req.getProbabilite() != null ? req.getProbabilite() : 1)
                * (req.getImpact() != null ? req.getImpact() : 1);

        risque.setCode(req.getCode());
        risque.setDescription(req.getDescription());
        risque.setCategorie(req.getCategorie());
        risque.setProbabilite(req.getProbabilite());
        risque.setImpact(req.getImpact());
        risque.setCriticite(criticite);
        risque.setStatut(req.getStatut());
        risque.setMesuresMitigation(req.getMesuresMitigation());
        risque.setDateIdentification(req.getDateIdentification());
        risque.setDateEcheance(req.getDateEcheance());

        return risqueRepository.save(risque);
    }

    public List<Risque> getByProjet(Long projetId) {
        return risqueRepository
                .findByProjetIdOrderByDateIdentificationDesc(projetId);
    }

    public void delete(Long id) {
        risqueRepository.deleteById(id);
    }
}