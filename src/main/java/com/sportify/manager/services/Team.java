package com.sportify.manager.services;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant une équipe sportive.
 */
public class Team {
    private int id;
    private String nom;
    private String categorie;
    private int clubId;
    private String coachId; // ID du coach (String car User.id est String)
    private Integer typeSportId; // Nullable
    private List<String> players; // Liste des IDs des joueurs

    /**
     * Constructeur complet.
     */
    public Team(int id, String nom, String categorie, int clubId, String coachId, Integer typeSportId) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.clubId = clubId;
        this.coachId = coachId;
        this.typeSportId = typeSportId;
        this.players = new ArrayList<>();
    }

    /**
     * Constructeur sans ID (pour création).
     */
    public Team(String nom, String categorie, int clubId, String coachId, Integer typeSportId) {
        this(0, nom, categorie, clubId, coachId, typeSportId);
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public Integer getTypeSportId() {
        return typeSportId;
    }

    public void setTypeSportId(Integer typeSportId) {
        this.typeSportId = typeSportId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String playerId) {
        if (!players.contains(playerId)) {
            players.add(playerId);
        }
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", clubId=" + clubId +
                ", coachId='" + coachId + '\'' +
                ", typeSportId=" + typeSportId +
                ", players=" + players.size() +
                '}';
    }
}
