package com.sportify.manager.services;

public class Statistique {
    private String type;    // Libellé de la stat (ex: "Taux de victoire", "Total Points")
    private double valeur;  // La valeur numérique calculée
    private String periode; // La période concernée (ex: "Saison 2024")
    private String unite;   // Optionnel (ex: "%", "buts", "points")

    // Constructeur
    public Statistique(String type, double valeur, String periode, String unite) {
        this.type = type;
        this.valeur = valeur;
        this.periode = periode;
        this.unite = unite;
    }

    // Getters
    public String getType() { return type; }
    public double getValeur() { return valeur; }
    public String getPeriode() { return periode; }
    public String getUnite() { return unite; }

    // Méthode utilitaire pour l'affichage
    @Override
    public String toString() {
        return type + " : " + valeur + " " + unite + " (" + periode + ")";
    }
}