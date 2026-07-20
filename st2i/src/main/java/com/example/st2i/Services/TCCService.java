package com.example.st2i.Services;

import com.example.st2i.DTO.TCCRequest;
import com.example.st2i.DTO.TCCResponse;
import com.example.st2i.Entities.TCC;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.TCCRepository;
import com.example.st2i.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TCCService {

    @Autowired private TCCRepository tccRepository;
    @Autowired private UserRepository userRepository;

    public TCCResponse create(TCCRequest request) {
        User user = userRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        TCC tcc = TCC.builder()
                .utilisateur(user)
                .annee(request.getAnnee())
                .tccBase(request.getTccBase())
                .tccAvecFG(request.getTccAvecFG())
                .build();

        return toDTO(tccRepository.save(tcc));
    }

    public List<TCCResponse> getByUser(Long userId) {
        return tccRepository.findByUtilisateurId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TCCResponse> getAll() {
        return tccRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TCCResponse update(Long id, TCCRequest request) {
        TCC existing = tccRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TCC non trouvé"));

        existing.setAnnee(request.getAnnee());
        existing.setTccBase(request.getTccBase());
        existing.setTccAvecFG(request.getTccAvecFG());

        return toDTO(tccRepository.save(existing));
    }

    public void delete(Long id) {
        tccRepository.deleteById(id);
    }

    private TCCResponse toDTO(TCC t) {
        double tccBase   = t.getTccBase()   != null ? t.getTccBase()   : 0;
        double fraisGen  = t.getTccAvecFG() != null ? t.getTccAvecFG() : 0;

        Double tarifHJ = (tccBase + fraisGen) / 264.0;

        return new TCCResponse(
                t.getId(),
                t.getAnnee() != null ? t.getAnnee().toString() : null,
                t.getTccBase(),
                t.getTccAvecFG(),
                tarifHJ,
                new TCCResponse.UserSummary(
                        t.getUtilisateur().getId(),
                        t.getUtilisateur().getNom(),
                        t.getUtilisateur().getPrenom()
                )
        );
    }
}