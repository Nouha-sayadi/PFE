package com.example.st2i.DTO;

import lombok.Data;

import java.util.List;
@Data
public class UserPermissionsResponse {
    private String username;
    private String email;
    private String profil;
    private List<PermissionDTO> permissions;
}
