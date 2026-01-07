package com.sportify.manager.services;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private int clubID;
    private String name;
    private String description;
    private String type;
    private String meetingSchedule;
    private int maxCapacity;
    private String status;
    private String requirements;
    private List<User> members;
    private int currentMemberCount; // NOUVEAU : Pour l'affichage rapide dans le tableau

    public Club(int clubID, String name, String description, String type, String meetingSchedule, int maxCapacity) {
        this.clubID = clubID;
        this.name = name;
        this.description = description;
        this.type = type;
        this.meetingSchedule = meetingSchedule;
        this.maxCapacity = maxCapacity;
        this.status = "Active";
        this.requirements = "";
        this.members = new ArrayList<>();
        this.currentMemberCount = 0; // Initialisé à 0
    }

    // --- LOGIQUE MÉTIER ---

    public boolean isFull() {
        // On utilise soit la liste si elle est chargée, soit le compteur
        int count = (members != null && !members.isEmpty()) ? members.size() : currentMemberCount;
        return count >= maxCapacity;
    }

    // --- GETTERS ET SETTERS ---

    // NOUVEAU GETTER pour JavaFX (PropertyValueFactory utilisera "currentMemberCount")
    public int getCurrentMemberCount() {
        return currentMemberCount;
    }

    public void setCurrentMemberCount(int currentMemberCount) {
        this.currentMemberCount = currentMemberCount;
    }

    public int getClubID() { return clubID; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getMeetingSchedule() { return meetingSchedule; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getStatus() { return status; }
    public List<User> getMembers() { return members; }
    public String getRequirements() { return requirements; }

    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setMeetingSchedule(String meetingSchedule) { this.meetingSchedule = meetingSchedule; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Club{" +
                "id=" + clubID +
                ", name='" + name + '\'' +
                ", count=" + currentMemberCount + "/" + maxCapacity +
                '}';
    }
}