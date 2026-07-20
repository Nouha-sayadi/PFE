package com.example.st2i.Entities;

public class ProjetDocument {
    public Long id;
    public String titre;
    public String texte;
    public double[] vector;

    public ProjetDocument(Long id, String titre, String texte, double[] vector) {
        this.id = id;
        this.titre = titre;
        this.texte = texte;
        this.vector = vector;
    }
}
