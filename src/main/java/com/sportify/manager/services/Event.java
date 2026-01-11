package com.sportify.manager.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private int id;
    private String nom;
    private String description;
    private LocalDateTime dateDebut;
    private int dureeMinutes;
    private String lieu;
    private String type; // MEETING, SOCIAL, TOURNAMENT...
    private int clubId;
    private String createurId;

    // Map stockant l'ID utilisateur et son statut (PRESENT, ABSENT...)
    private Map<String, String> participants;

    public Event(int id, String nom, String description, LocalDateTime dateDebut, int dureeMinutes,
                 String lieu, String type, int clubId, String createurId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dureeMinutes = dureeMinutes;
        this.lieu = lieu;
        this.type = type;
        this.clubId = clubId;
        this.createurId = createurId;
        this.participants = new HashMap<>();
    }

    public Event(String nom, String description, LocalDateTime dateDebut, int dureeMinutes,
                 String lieu, String type, int clubId, String createurId) {
        this(0, nom, description, dateDebut, dureeMinutes, lieu, type, clubId, createurId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getCreateurId() { return createurId; }
    public void setCreateurId(String createurId) { this.createurId = createurId; }

    public Map<String, String> getParticipants() { return participants; }
    public void setParticipants(Map<String, String> participants) { this.participants = participants; }
    public void addParticipant(String userId, String status) { this.participants.put(userId, status); }
}
