package com.example.st2i.Services;

import com.example.st2i.DTO.ContratRequest;
import com.example.st2i.Entities.Contrat;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.ContratRepository;
import com.example.st2i.Repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;
    @Autowired private ProjetRepository projetRepository;

    public Contrat create(ContratRequest req) {
        Projet projet = projetRepository.findById(req.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        if (!contratRepository.findByProjetId(req.getProjetId()).isEmpty()) {
            throw new IllegalStateException("Ce projet a déjà un contrat. Modifiez-le ou supprimez-le avant d'en créer un nouveau.");
        }
        Contrat c = Contrat.builder()
                .projet(projet)
                .numeroContrat(req.getNumeroContrat())
                .intitule(req.getIntitule())
                .montantTotal(req.getMontantTotal())
                .dateSignature(req.getDateSignature())
                .dateEcheance(req.getDateEcheance())
                .conditionsPaiement(req.getConditionsPaiement())
                .build();
        return contratRepository.save(c);
    }

    public List<Contrat> getByProjet(Long projetId) {
        return contratRepository.findByProjetId(projetId);
    }

    public Contrat update(Long id, ContratRequest req) {
        Contrat c = contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));
        c.setNumeroContrat(req.getNumeroContrat());
        c.setIntitule(req.getIntitule());
        c.setMontantTotal(req.getMontantTotal());
        c.setDateSignature(req.getDateSignature());
        c.setDateEcheance(req.getDateEcheance());
        c.setConditionsPaiement(req.getConditionsPaiement());
        return contratRepository.save(c);
    }

    public void delete(Long id) { contratRepository.deleteById(id); }
}
