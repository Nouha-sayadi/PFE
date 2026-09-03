package com.example.st2i.Services;

import com.example.st2i.DTO.LivrableRequest;
import com.example.st2i.DTO.LivrableResponse;
import com.example.st2i.Entities.Livrable;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.LivrableRepository;
import com.example.st2i.Repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivrableService {

    @Autowired private LivrableRepository livrableRepository;
    @Autowired private ProjetRepository projetRepository;

    public LivrableResponse create(LivrableRequest request) {
        Projet projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        Livrable livrable = Livrable.builder()
                .projet(projet)
                .numero(request.getNumero())
                .designation(request.getDesignation())
                .phase(request.getPhase())
                .dateLivraisonPrevue(request.getDateLivraisonPrevue())
                .dateLivraisonReelle(request.getDateLivraisonReelle())
                .realise(request.getRealise() != null ? request.getRealise() : false)
                .livre(request.getLivre() != null ? request.getLivre() : false)
                .commentaire(request.getCommentaire())
                .build();


        calculerDeliveryEtEcart(livrable);

        return toDTO(livrableRepository.save(livrable));
    }

    public LivrableResponse update(Long id, LivrableRequest request) {
        Livrable livrable = livrableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livrable non trouvé"));

        livrable.setNumero(request.getNumero());
        livrable.setDesignation(request.getDesignation());
        livrable.setPhase(request.getPhase());
        livrable.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        livrable.setDateLivraisonReelle(request.getDateLivraisonReelle());
        livrable.setRealise(request.getRealise() != null ? request.getRealise() : false);
        livrable.setLivre(request.getLivre() != null ? request.getLivre() : false);
        livrable.setCommentaire(request.getCommentaire());


        calculerDeliveryEtEcart(livrable);

        return toDTO(livrableRepository.save(livrable));
    }

    public List<LivrableResponse> getByProjet(Long projetId) {
        return livrableRepository.findByProjetIdOrderByNumero(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }


    public double getDeliveryGlobal(Long projetId) {
        long total = livrableRepository.countByProjetId(projetId);
        if (total == 0) return 0;
        long livres = livrableRepository.countByProjetIdAndLivreTrue(projetId);
        return (double) livres / total;
    }

    public void delete(Long id) {
        livrableRepository.deleteById(id);
    }


    private void calculerDeliveryEtEcart(Livrable livrable) {
        // delivery = 1 si livré, 0 sinon
        livrable.setDelivery(Boolean.TRUE.equals(livrable.getLivre()) ? 1.0 : 0.0);

        // écart en jours = dateReelle - datePrevue
        if (livrable.getDateLivraisonPrevue() != null
                && livrable.getDateLivraisonReelle() != null) {
            long ecart = ChronoUnit.DAYS.between(
                    livrable.getDateLivraisonPrevue(),
                    livrable.getDateLivraisonReelle()
            );
            livrable.setEcartJours((int) ecart);
        } else {
            livrable.setEcartJours(null);
        }
    }

    private LivrableResponse toDTO(Livrable l) {
        // Calcul delivery global
        double deliveryGlobal = getDeliveryGlobal(l.getProjet().getId());

        LivrableResponse r = new LivrableResponse();
        r.setId(l.getId());
        r.setNumero(l.getNumero());
        r.setDesignation(l.getDesignation());
        r.setPhase(l.getPhase());
        r.setDateLivraisonPrevue(l.getDateLivraisonPrevue() != null
                ? l.getDateLivraisonPrevue().toString() : null);
        r.setDateLivraisonReelle(l.getDateLivraisonReelle() != null
                ? l.getDateLivraisonReelle().toString() : null);
        r.setRealise(l.getRealise());
        r.setLivre(l.getLivre());
        r.setDelivery(l.getDelivery());
        r.setEcartJours(l.getEcartJours());
        r.setCommentaire(l.getCommentaire());
        r.setDeliveryGlobal(deliveryGlobal);
        r.setProjet(new LivrableResponse.ProjetSummary(
                l.getProjet().getId(),
                l.getProjet().getTitre()
        ));
        return r;
    }
}