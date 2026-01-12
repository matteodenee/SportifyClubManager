package com.sportify.manager.services;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private int clubId;
    private String name;
    private String description;
    private int sportId;
    private String type;
    private int maxCapacity;
    private String status;
    private List<User> members;
    private int currentMemberCount;
    private String managerId;

    public Club(int clubId, String name, String description, int sportId, String type, int maxCapacity) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.sportId = sportId;
        this.type = type;
        this.maxCapacity = maxCapacity;
        this.status = "Active";
        this.members = new ArrayList<>();
        this.currentMemberCount = 0;
        this.managerId = null;
    }

    public Club(int clubId, String name, String description, int sportId, String type, int maxCapacity, String managerId) {
        this(clubId, name, description, sportId, type, maxCapacity);
        this.managerId = managerId;
    }


    public int getSportId() {
        return sportId;
    }


    public int getClubId() {
        return clubId;
    }


    public int getClubID() {
        return getClubId();
    }


    @Override
    public String toString() {
        return name;
    }


    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getStatus() { return status; }
    public List<User> getMembers() { return members; }
    public int getCurrentMemberCount() { return currentMemberCount; }
    public String getManagerId() { return managerId; }


    public void setSportId(int sportId) { this.sportId = sportId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrentMemberCount(int currentMemberCount) { this.currentMemberCount = currentMemberCount; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
}
