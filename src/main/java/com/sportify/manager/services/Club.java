package com.sportify.manager.services;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private int clubId; // Changé clubID en clubId pour correspondre aux standards Java
    private String name;
    private String description;
    private int sportId; // Ajouté car nécessaire pour la planification des Matchs
    private String type; // Tu peux garder type pour le libellé (ex: "Football")
    private String meetingSchedule;
    private int maxCapacity;
    private String status;
    private String requirements;
    private List<User> members;
    private int currentMemberCount;

    public Club(int clubId, String name, String description, int sportId, String type, String meetingSchedule, int maxCapacity) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.sportId = sportId;
        this.type = type;
        this.meetingSchedule = meetingSchedule;
        this.maxCapacity = maxCapacity;
        this.status = "Active";
        this.requirements = "";
        this.members = new ArrayList<>();
        this.currentMemberCount = 0;
    }

    // --- NOUVEAU GETTER POUR LE SPORT ---
    public int getSportId() {
        return sportId;
    }

    // --- CORRECTION DU NOM POUR MATCH AVEC DASHBOARD ---
    public int getClubId() {
        return clubId;
    }
    // Dans Club.java, ajoute ceci :
    public int getClubID() {
        return getClubId(); // Redirige l'ancien nom vers le nouveau
    }

    // --- TOSTRING PROPRE POUR LES COMBOBOX ---
    @Override
    public String toString() {
        return name; // Indispensable pour que l'admin voie le nom et pas l'objet
    }

    // --- AUTRES GETTERS (GARDÉS) ---
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getMeetingSchedule() { return meetingSchedule; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getStatus() { return status; }
    public List<User> getMembers() { return members; }
    public String getRequirements() { return requirements; }
    public int getCurrentMemberCount() { return currentMemberCount; }

    // --- SETTERS ---
    public void setSportId(int sportId) { this.sportId = sportId; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setMeetingSchedule(String meetingSchedule) { this.meetingSchedule = meetingSchedule; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrentMemberCount(int currentMemberCount) { this.currentMemberCount = currentMemberCount; }
}