package com.example.st2i.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatutProjet {
    A_DEMARRER,
    EN_COURS,
    TERMINE,
    EN_ATTENTE,
    ANNULE,
    PLANIFIE;

    @JsonCreator
    public static StatutProjet fromValue(String value) {
        if (value == null) return A_DEMARRER;
        for (StatutProjet s : values()) {
            if (s.name().equalsIgnoreCase(value.trim())) return s;
        }
        return A_DEMARRER;
    }}