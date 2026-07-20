package com.example.st2i.Services;

import com.example.st2i.Entities.*;
import com.example.st2i.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjetService {

    @Autowired private ProjetRepository projetRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private BailleurRepository bailleurRepository;
    @Autowired private PartenaireRepository partenaireRepository;
    @Autowired private DeviseRepository deviseRepository;
    @Autowired private ModeleBusinessRepository modeleBusinessRepository;
    @Autowired private PointageRepository pointageRepository;
    @Autowired private CoutPrevRepository coutPrevRepository;
    @Autowired private CoutReelRepository coutReelRepository ;
    @Autowired private EstimationRepository estimationRepository;
    @Autowired private AffectationRepository affectationRepository;
    @Autowired private LivrableRepository livrableRepository;
    @Autowired private EcheanceFacturationRepository echeanceFacturationRepository;
    @Autowired private KpiMensuelProjetRepository kpiMensuelProjetRepository;
    @Autowired private ContratRepository contratRepository;
    @Autowired private AvenantRepository avenantRepository;
    @Autowired private RisqueRepository risqueRepository;
    @Autowired private ActionRepository actionRepository;




    public Projet save(Projet projet) {

        if (projet.getMontantTotal() == null && projet.getBudgetInitial() != null) {
            projet.setMontantTotal(projet.getBudgetInitial());
        }

        // ✅ Charger Client
        if (projet.getClient() != null && projet.getClient().getId() != null) {
            Client client = clientRepository.findById(projet.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            projet.setClient(client);
        }

        // ✅ Charger Bailleur
        if (projet.getBailleur() != null && projet.getBailleur().getId() != null) {
            Bailleur bailleur = bailleurRepository.findById(projet.getBailleur().getId())
                    .orElseThrow(() -> new RuntimeException("Bailleur non trouvé"));
            projet.setBailleur(bailleur);
        }

        // ✅ Charger Partenaire
        if (projet.getPartenaire() != null && projet.getPartenaire().getId() != null) {
            Partenaire partenaire = partenaireRepository.findById(projet.getPartenaire().getId())
                    .orElseThrow(() -> new RuntimeException("Partenaire non trouvé"));
            projet.setPartenaire(partenaire);
        }

        // ✅ Charger Devise
        if (projet.getDevise() != null && projet.getDevise().getId() != null) {
            Devise devise = deviseRepository.findById(projet.getDevise().getId())
                    .orElseThrow(() -> new RuntimeException("Devise non trouvée"));
            projet.setDevise(devise);
        }

        if (projet.getModeleBusiness() != null && projet.getModeleBusiness().getId() != null) {
            ModeleBusiness modele = modeleBusinessRepository.findById(projet.getModeleBusiness().getId())
                    .orElseThrow(() -> new RuntimeException("Modèle Business non trouvé"));
            projet.setModeleBusiness(modele);
        }

        return projetRepository.save(projet);
    }

    public List<Projet> findAll() {
        return projetRepository.findAll();
    }

    public Projet findById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
    }

    public Projet update(Long id, Projet projet) {

        Projet existing = findById(id);

        existing.setTitre(projet.getTitre());
        existing.setDescription(projet.getDescription());

        existing.setBudgetInitial(projet.getBudgetInitial());
        existing.setMontantTotal(projet.getMontantTotal());

        existing.setStatut(projet.getStatut());
        existing.setTauxAvancement(projet.getTauxAvancement());

        if (projet.getDateDemarrage() != null)
            existing.setDateDemarrage(projet.getDateDemarrage());

        if (projet.getDateFinPrevu() != null)
            existing.setDateFinPrevu(projet.getDateFinPrevu());

        // ✅ CLIENT
        if (projet.getClient() != null && projet.getClient().getId() != null) {
            Client client = clientRepository.findById(projet.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            existing.setClient(client);
        } else {
            existing.setClient(null);
        }

        // ✅ BAILLEUR
        if (projet.getBailleur() != null && projet.getBailleur().getId() != null) {
            Bailleur bailleur = bailleurRepository.findById(projet.getBailleur().getId())
                    .orElseThrow(() -> new RuntimeException("Bailleur non trouvé"));
            existing.setBailleur(bailleur);
        } else {
            existing.setBailleur(null);
        }

        // ✅ PARTENAIRE
        if (projet.getPartenaire() != null && projet.getPartenaire().getId() != null) {
            Partenaire partenaire = partenaireRepository.findById(projet.getPartenaire().getId())
                    .orElseThrow(() -> new RuntimeException("Partenaire non trouvé"));
            existing.setPartenaire(partenaire);
        } else {
            existing.setPartenaire(null);
        }

        // ✅ DEVISE
        if (projet.getDevise() != null && projet.getDevise().getId() != null) {
            Devise devise = deviseRepository.findById(projet.getDevise().getId())
                    .orElseThrow(() -> new RuntimeException("Devise non trouvée"));
            existing.setDevise(devise);
        } else {
            existing.setDevise(null);
        }

        // ✅ MODELE BUSINESS
        if (projet.getModeleBusiness() != null && projet.getModeleBusiness().getId() != null) {
            ModeleBusiness modele = modeleBusinessRepository.findById(projet.getModeleBusiness().getId())
                    .orElseThrow(() -> new RuntimeException("Modèle Business non trouvé"));
            existing.setModeleBusiness(modele);
        } else {
            existing.setModeleBusiness(null);
        }

        return projetRepository.save(existing);
    }
@Transactional
    public void delete(Long id) {
        pointageRepository.deleteByProjetId(id);
        coutReelRepository.deleteByProjetId(id);
        coutPrevRepository.deleteByProjetId(id);
        estimationRepository.deleteByProjetId(id);
        affectationRepository.deleteByProjetId(id);
        livrableRepository.deleteByProjetId(id);
        echeanceFacturationRepository.deleteByProjetId(id);
        kpiMensuelProjetRepository.deleteByProjetId(id);
        contratRepository.deleteByProjetId(id);
        projetRepository.deleteById(id);
        avenantRepository.deleteByProjetId(id);
        actionRepository.deleteByProjetId(id);
        risqueRepository.deleteByProjetId(id);

    }

}