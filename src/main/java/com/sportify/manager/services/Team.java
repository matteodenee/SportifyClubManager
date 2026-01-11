package com.sportify.manager.services;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int id;
    private String nom;
    private String categorie;
    private int clubId;
    private String coachId;
    private Integer typeSportId;
    private List<User> players;

    public Team(int id, String nom, String categorie, int clubId, String coachId, Integer typeSportId) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.clubId = clubId;
        this.coachId = coachId;
        this.typeSportId = typeSportId;
        this.players = new ArrayList<>();
    }

    public Team(String nom, String categorie, int clubId, String coachId, Integer typeSportId) {
        this(0, nom, categorie, clubId, coachId, typeSportId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getCoachId() { return coachId; }
    public void setCoachId(String coachId) { this.coachId = coachId; }

    public Integer getTypeSportId() { return typeSportId; }
    public void setTypeSportId(Integer typeSportId) { this.typeSportId = typeSportId; }

    public List<User> getPlayers() { return players; }
    public void setPlayers(List<User> players) { this.players = players; }
    public void addPlayer(User player) { this.players.add(player); }
}
