package com.example.st2i.DTO;

import lombok.Data;

import java.util.List;


@Data
public class ProfilCreateRequest {
    private String nom;
    private String code;
    private List<Long> permissionIds;
}
