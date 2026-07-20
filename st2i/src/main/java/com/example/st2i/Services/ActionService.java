package com.example.st2i.Services;

import com.example.st2i.DTO.ActionRequest;
import com.example.st2i.Entities.Action;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActionService {

    @Autowired private ActionRepository actionRepository;
    @Autowired private ProjetRepository projetRepository;
    @Autowired private RisqueRepository risqueRepository;

    public Action create(ActionRequest req) {
        Projet projet = projetRepository.findById(req.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        Action action = Action.builder()
                .projet(projet)
                .code(req.getCode())
                .description(req.getDescription())
                .typeAction(req.getTypeAction())
                .responsable(req.getResponsable())
                .datePrevue(req.getDatePrevue())
                .dateReelle(req.getDateReelle())
                .statut(req.getStatut() != null ? req.getStatut() : "A_FAIRE")
                .commentaire(req.getCommentaire())
                .build();

        // ✅ Lier au risque si fourni
        if (req.getRisqueId() != null) {
            risqueRepository.findById(req.getRisqueId())
                    .ifPresent(action::setRisque);
        }

        return actionRepository.save(action);
    }

    public Action update(Long id, ActionRequest req) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));

        action.setCode(req.getCode());
        action.setDescription(req.getDescription());
        action.setTypeAction(req.getTypeAction());
        action.setResponsable(req.getResponsable());
        action.setDatePrevue(req.getDatePrevue());
        action.setDateReelle(req.getDateReelle());
        action.setStatut(req.getStatut());
        action.setCommentaire(req.getCommentaire());

        if (req.getRisqueId() != null) {
            risqueRepository.findById(req.getRisqueId())
                    .ifPresent(action::setRisque);
        } else {
            action.setRisque(null);
        }

        return actionRepository.save(action);
    }

    public List<Action> getByProjet(Long projetId) {
        return actionRepository
                .findByProjetIdOrderByDatePrevueDesc(projetId);
    }

    public void delete(Long id) {
        actionRepository.deleteById(id);
    }
}