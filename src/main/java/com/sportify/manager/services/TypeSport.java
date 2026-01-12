package com.sportify.manager.services;

import java.util.ArrayList;
import java.util.List;

public class TypeSport {
    private int id;
    private String nom;
    private String description;
    private int nbJoueurs;
    private List<String> roles;
    private List<String> statistiques;


    public TypeSport(String nom, String description, int nbJoueurs, List<String> roles, List<String> statistiques) {
        this.id = 0;
        this.nom = nom;
        this.description = description;
        this.nbJoueurs = nbJoueurs;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.statistiques = statistiques != null ? new ArrayList<>(statistiques) : new ArrayList<>();
    }


    public TypeSport(int id, String nom, String description, int nbJoueurs) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.nbJoueurs = nbJoueurs;
        this.roles = new ArrayList<>();
        this.statistiques = new ArrayList<>();
    }


    public TypeSport(int id, String nom, String description, int nbJoueurs, List<String> roles, List<String> statistiques) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.nbJoueurs = nbJoueurs;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.statistiques = statistiques != null ? new ArrayList<>(statistiques) : new ArrayList<>();
    }

    // --- GETTERS ET SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getNbJoueurs() { return nbJoueurs; }
    public void setNbJoueurs(int nbJoueurs) { this.nbJoueurs = nbJoueurs; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<String> getStatistiques() { return statistiques; }
    public void setStatistiques(List<String> statistiques) { this.statistiques = statistiques; }

    @Override
    public String toString() {
        return nom;
    }
}