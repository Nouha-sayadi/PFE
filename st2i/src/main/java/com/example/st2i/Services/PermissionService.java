package com.example.st2i.Services;

import com.example.st2i.Entities.Permission;
import com.example.st2i.Repositories.PermissionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    @PostConstruct
    public void initPermissions() {

        Permission admin = getOrCreate(null, "ADMINISTRATION",
                "Administration", "ADMINISTRATION", null);

        Permission gestionUsers = getOrCreate(admin, "GESTION_USERS",
                "Gestion des utilisateurs", "GESTION_USERS", null);

        Permission gestionProfils = getOrCreate(admin, "GESTION_PROFILS",
                "Gestion des profils", "GESTION_PROFILS", null);
        Permission gestionTCC = getOrCreate(admin, "GESTION_TCC",
                "Gestion des TCC", "GESTION_TCC", null);

        Permission gestionPermissions = getOrCreate(admin, "GESTION_PERMISSIONS",
                "Gestion des permissions", "GESTION_PERMISSIONS", null);
        Permission gestionProjets = getOrCreate(admin, "GESTION_PROJETS",
                "Gestion des projets", "GESTION_PROJETS", null);

        Permission gestionNomenclatures = getOrCreate(admin, "GESTION_NOMENCLATURES",
                "Gestion des nomenclatures", "GESTION_NOMENCLATURES", null);

        getOrCreate(gestionUsers, "GET_USER",    "Lire les utilisateurs",    "GESTION_USERS", "Liste Utilisateurs");
        getOrCreate(gestionUsers, "POST_USER",   "Créer un utilisateur",     "GESTION_USERS", "Liste Utilisateurs");
        getOrCreate(gestionUsers, "PUT_USER",    "Modifier un utilisateur",  "GESTION_USERS", "Liste Utilisateurs");
        getOrCreate(gestionUsers, "DELETE_USER", "Supprimer un utilisateur", "GESTION_USERS", "Liste Utilisateurs");

        getOrCreate(gestionProfils, "GET_PROFIL",    "Lire les profils",    "GESTION_PROFILS", "Profils");
        getOrCreate(gestionProfils, "POST_PROFIL",   "Créer un profil",     "GESTION_PROFILS", "Profils");
        getOrCreate(gestionProfils, "PUT_PROFIL",    "Modifier un profil",  "GESTION_PROFILS", "Profils");
        getOrCreate(gestionProfils, "DELETE_PROFIL", "Supprimer un profil", "GESTION_PROFILS", "Profils");

        getOrCreate(gestionPermissions, "GET_PERMISSION",    "Lire les permissions",    "GESTION_PERMISSIONS", "Permissions");
        getOrCreate(gestionPermissions, "POST_PERMISSION",   "Créer une permission",    "GESTION_PERMISSIONS", "Permissions");
        getOrCreate(gestionPermissions, "PUT_PERMISSION",    "Modifier une permission", "GESTION_PERMISSIONS", "Permissions");
        getOrCreate(gestionPermissions, "DELETE_PERMISSION", "Supprimer une permission","GESTION_PERMISSIONS", "Permissions");

        getOrCreate(gestionProjets, "GET_PROJET",    "Lire les projets",    "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "POST_PROJET",   "Créer un projet",     "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "PUT_PROJET",    "Modifier un projet",  "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "DELETE_PROJET", "Supprimer un projet", "GESTION_PROJETS", "Projets");

        getOrCreate(gestionTCC, "GET_TCC",    "Lire les TCC",     "GESTION_TCC", "TCC");
        getOrCreate(gestionTCC, "POST_TCC",   "Créer un TCC",     "GESTION_TCC", "TCC");
        getOrCreate(gestionTCC, "PUT_TCC",    "Modifier un TCC",  "GESTION_TCC", "TCC");
        getOrCreate(gestionTCC, "DELETE_TCC", "Supprimer un TCC", "GESTION_TCC", "TCC");

        // ── Clients ──
        getOrCreate(gestionNomenclatures, "GET_CLIENT",    "Voir les clients",      "GESTION_NOMENCLATURES", "Clients");
        getOrCreate(gestionNomenclatures, "POST_CLIENT",   "Créer un client",       "GESTION_NOMENCLATURES", "Clients");
        getOrCreate(gestionNomenclatures, "PUT_CLIENT",    "Modifier un client",    "GESTION_NOMENCLATURES", "Clients");
        getOrCreate(gestionNomenclatures, "DELETE_CLIENT", "Supprimer un client",   "GESTION_NOMENCLATURES", "Clients");

// ── Bailleurs ──
        getOrCreate(gestionNomenclatures, "GET_BAILLEUR",    "Voir les bailleurs",    "GESTION_NOMENCLATURES", "Bailleurs");
        getOrCreate(gestionNomenclatures, "POST_BAILLEUR",   "Créer un bailleur",     "GESTION_NOMENCLATURES", "Bailleurs");
        getOrCreate(gestionNomenclatures, "PUT_BAILLEUR",    "Modifier un bailleur",  "GESTION_NOMENCLATURES", "Bailleurs");
        getOrCreate(gestionNomenclatures, "DELETE_BAILLEUR", "Supprimer un bailleur", "GESTION_NOMENCLATURES", "Bailleurs");

// ── Partenaires ──
        getOrCreate(gestionNomenclatures, "GET_PARTENAIRE",    "Voir les partenaires",    "GESTION_NOMENCLATURES", "Partenaires");
        getOrCreate(gestionNomenclatures, "POST_PARTENAIRE",   "Créer un partenaire",     "GESTION_NOMENCLATURES", "Partenaires");
        getOrCreate(gestionNomenclatures, "PUT_PARTENAIRE",    "Modifier un partenaire",  "GESTION_NOMENCLATURES", "Partenaires");
        getOrCreate(gestionNomenclatures, "DELETE_PARTENAIRE", "Supprimer un partenaire", "GESTION_NOMENCLATURES", "Partenaires");

// ── Devises ──
        getOrCreate(gestionNomenclatures, "GET_DEVISE",    "Voir les devises",    "GESTION_NOMENCLATURES", "Devises");
        getOrCreate(gestionNomenclatures, "POST_DEVISE",   "Créer une devise",    "GESTION_NOMENCLATURES", "Devises");
        getOrCreate(gestionNomenclatures, "PUT_DEVISE",    "Modifier une devise", "GESTION_NOMENCLATURES", "Devises");
        getOrCreate(gestionNomenclatures, "DELETE_DEVISE", "Supprimer une devise","GESTION_NOMENCLATURES", "Devises");

// ── Modèles Business ──
        getOrCreate(gestionNomenclatures, "GET_MODELE",    "Voir les modèles",    "GESTION_NOMENCLATURES", "Modèles Business");
        getOrCreate(gestionNomenclatures, "POST_MODELE",   "Créer un modèle",     "GESTION_NOMENCLATURES", "Modèles Business");
        getOrCreate(gestionNomenclatures, "PUT_MODELE",    "Modifier un modèle",  "GESTION_NOMENCLATURES", "Modèles Business");
        getOrCreate(gestionNomenclatures, "DELETE_MODELE", "Supprimer un modèle", "GESTION_NOMENCLATURES", "Modèles Business");

        // ── Interface "Projets" (gestion admin) ──
        getOrCreate(gestionProjets, "GET_PROJET",    "Lire les projets",     "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "POST_PROJET",   "Créer un projet",      "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "PUT_PROJET",    "Modifier un projet",   "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "DELETE_PROJET", "Supprimer un projet",  "GESTION_PROJETS", "Projets");
        getOrCreate(gestionProjets, "AFFECT_PROJET", "Affecter des membres", "GESTION_PROJETS", "Projets");

// ── Interface "Mes Projets" (espace membre) ──
        getOrCreate(gestionProjets, "GET_MES_PROJETS", "Voir mes projets affectés", "GESTION_PROJETS", "Mes Projets");
        getOrCreate(gestionProjets, "GET_MA_CHARGE",   "Voir ma charge prévue",     "GESTION_PROJETS", "Mes Projets");
        getOrCreate(gestionProjets, "POST_POINTAGE",   "Saisir mon pointage",       "GESTION_PROJETS", "Mes Projets");

    }

    private Permission getOrCreate(Permission parent, String code,
                                   String libelle, String module, String interfaceName) {
        return permissionRepository.findByCode(code).orElseGet(() -> {
            Permission p = Permission.builder()
                    .code(code)
                    .libelle(libelle)
                    .module(module)
                    .parent(parent)
                    .interfaceName(interfaceName)  // ← NOUVEAU
                    .build();
            return permissionRepository.save(p);
        });
    }

    public List<Permission> getAll() {
        return permissionRepository.findByParentIsNotNull();
    }

    public List<Permission> getByModule(String module) {
        return permissionRepository.findByModule(module);
    }

    public Permission getById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission introuvable"));
    }

    public List<Permission> getTree() {
        return permissionRepository.findByParentIsNull();
    }
}
