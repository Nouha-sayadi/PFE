package com.example.st2i.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TypeEntiteDocument {
    CONTRAT,
    LIVRABLE,
    ECHEANCE_FACTURATION;

    @JsonCreator
    public static TypeEntiteDocument fromValue(String value) {
        if (value == null) return null;
        for (TypeEntiteDocument t : values()) {
            if (t.name().equalsIgnoreCase(value.trim())) return t;
        }
        throw new IllegalArgumentException("Type d'entité document inconnu : " + value);
    }
}
