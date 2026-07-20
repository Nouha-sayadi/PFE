package com.example.st2i.RestController;

import com.example.st2i.Entities.Permission;
import com.example.st2i.Services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin("*")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public List<Permission> getAll() {
        return permissionService.getAll();
    }

    @GetMapping("/tree")
    public List<Permission> getTree() {
        return permissionService.getTree();
    }

    @GetMapping("/module/{module}")
    public List<Permission> getByModule(@PathVariable String module) {
        return permissionService.getByModule(module);
    }

    @GetMapping("/{id}")
    public Permission getById(@PathVariable Long id) {
        return permissionService.getById(id);
    }
}