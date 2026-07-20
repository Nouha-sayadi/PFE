package com.example.st2i.RestController;

import com.example.st2i.DTO.ProfilCreateRequest;
import com.example.st2i.Entities.Profil;
import com.example.st2i.Repositories.ProfilRepository;
import com.example.st2i.Services.ProfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profils")
@CrossOrigin("*")
public class ProfilController {
    @Autowired
    private ProfilService profilService;
    @Autowired
    private ProfilRepository profilRepository;


    @PostMapping ("/create")
    public Profil createProfil(@RequestBody ProfilCreateRequest request) {
        return profilService.createProfil(request);
    }

    @GetMapping
    public List<Profil> getAll() {
        return profilRepository.findAll();
    }

    @PutMapping("/{id}/permissions")
    public Profil updatePermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        return profilService.updatePermissions(id, permissionIds);
    }



}
