package com.example.st2i.Services;

import com.example.st2i.DTO.AvenantRequest;
import com.example.st2i.DTO.AvenantResponse;
import com.example.st2i.Entities.*;
import com.example.st2i.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvenantService {

    @Autowired private AvenantRepository avenantRepository;
    @Autowired private ProjetRepository projetRepository;
    @Autowired private ContratRepository contratRepository;

    @Transactional
    public AvenantResponse create(AvenantRequest req) {
        Projet projet = projetRepository.findById(req.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        double montantInitial = 0;

        if (projet.getMontantTotal() != null && projet.getMontantTotal() > 0) {
            montantInitial = projet.getMontantTotal();
        } else if (projet.getBudgetInitial() != null && projet.getBudgetInitial() > 0) {
            montantInitial = projet.getBudgetInitial();
        }

        double montantRevise = req.getMontantRevise() != null
                ? req.getMontantRevise() : montantInitial;

        double deltaMontant = montantRevise - montantInitial;

        Avenant avenant = Avenant.builder()
                .projet(projet)
                .numero(req.getNumero())
                .objet(req.getObjet())
                .montantInitial(montantInitial)
                .montantRevise(montantRevise)
                .deltaMontant(deltaMontant)
                .impactDelais(req.getImpactDelais())
                .dateSignature(req.getDateSignature())
                .dateEffet(req.getDateEffet())
                .statut(req.getStatut() != null ? req.getStatut() : "EN_ATTENTE")
                .commentaire(req.getCommentaire())
                .build();

        if (req.getContratId() != null) {
            contratRepository.findById(req.getContratId())
                    .ifPresent(avenant::setContrat);
        }

        if ("APPROUVE".equals(req.getStatut())) {
            appliquerSurProjet(projet, montantRevise, req.getImpactDelais());
        }

        return toDTO(avenantRepository.save(avenant));
    }

    @Transactional
    public AvenantResponse update(Long id, AvenantRequest req) {
        Avenant avenant = avenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avenant non trouvé"));

        Projet projet = avenant.getProjet();
        boolean wasApprouve = "APPROUVE".equals(avenant.getStatut());
        boolean nowApprouve = "APPROUVE".equals(req.getStatut());

        if (!wasApprouve && nowApprouve) {
            appliquerSurProjet(projet,
                    req.getMontantRevise(),
                    req.getImpactDelais());
        }

        avenant.setNumero(req.getNumero());
        avenant.setObjet(req.getObjet());
        avenant.setMontantRevise(req.getMontantRevise());
        avenant.setDeltaMontant(
                (req.getMontantRevise() != null ? req.getMontantRevise() : 0)
                        - (avenant.getMontantInitial() != null ? avenant.getMontantInitial() : 0)
        );
        avenant.setImpactDelais(req.getImpactDelais());
        avenant.setDateSignature(req.getDateSignature());
        avenant.setDateEffet(req.getDateEffet());
        avenant.setStatut(req.getStatut());
        avenant.setCommentaire(req.getCommentaire());

        return toDTO(avenantRepository.save(avenant));
    }

    public List<AvenantResponse> getByProjet(Long projetId) {
        return avenantRepository
                .findByProjetIdOrderByDateSignatureDesc(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        avenantRepository.deleteById(id);
    }

    private void appliquerSurProjet(Projet projet,
                                    Double montantRevise,
                                    Integer impactDelais) {
        if (montantRevise != null) {
            projet.setMontantTotal(montantRevise);
        }
        if (impactDelais != null && impactDelais != 0
                && projet.getDateFinPrevu() != null) {
            projet.setDateFinPrevu(
                    projet.getDateFinPrevu().plusDays(impactDelais)
            );
        }
        projetRepository.save(projet);
    }

    private AvenantResponse toDTO(Avenant a) {
        AvenantResponse r = new AvenantResponse();
        r.setId(a.getId());
        r.setNumero(a.getNumero());
        r.setObjet(a.getObjet());
        r.setMontantInitial(a.getMontantInitial());
        r.setMontantRevise(a.getMontantRevise());
        r.setDeltaMontant(a.getDeltaMontant());
        r.setImpactDelais(a.getImpactDelais());
        r.setDateSignature(a.getDateSignature() != null
                ? a.getDateSignature().toString() : null);
        r.setDateEffet(a.getDateEffet() != null
                ? a.getDateEffet().toString() : null);
        r.setStatut(a.getStatut());
        r.setCommentaire(a.getCommentaire());
        r.setProjetId(a.getProjet() != null ? a.getProjet().getId() : null);
        r.setContratId(a.getContrat() != null ? a.getContrat().getId() : null);
        return r;
    }
}