package com.example.st2i.DTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    private String code;
    private String libelle;
    private String module;
    @Column(name = "interface_name")
    private String interfaceName;
}
