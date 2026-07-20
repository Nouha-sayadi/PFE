package com.example.st2i.Services;

import com.example.st2i.DTO.ProfilCreateRequest;
import com.example.st2i.Entities.Permission;
import com.example.st2i.Entities.Profil;
import com.example.st2i.Repositories.PermissionRepository;
import com.example.st2i.Repositories.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfilService {
    @Autowired
    private ProfilRepository profilRepository;
    @Autowired
    private PermissionRepository permissionRepository;


    public Profil createProfil(ProfilCreateRequest request) {
        List<Permission> permissions = new ArrayList<>();
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions = permissionRepository.findAllById(request.getPermissionIds());
        }
        Profil profil = new Profil();
        profil.setNom(request.getNom());
        profil.setCode(request.getCode().toUpperCase());
        profil.setPermissions(permissions);
        return profilRepository.save(profil);
    }



    public List<Profil> getAllProfils() {
        return profilRepository.findAll();
    }

    public Profil getProfilById(Long id) {
        return profilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
    }

    public Profil updatePermissions(Long id, List<Long> permissionIds) {
        Profil profil = profilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        profil.setPermissions(permissions);
        return profilRepository.save(profil);
    }

    public void deleteProfil(Long id) {
        profilRepository.deleteById(id);
    }








}
