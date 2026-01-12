package com.sportify.manager.services;

import java.sql.Timestamp;

public class SmallEvent {
    private int id;
    private String type;
    private String description;
    private int teamId;
    private String playerId;
    private Timestamp timestamp;
    private String period;
    private Integer matchId;

    // Constructeur complet
    public SmallEvent(int id, String type, String description, int teamId, String playerId, Timestamp timestamp, String period, Integer matchId) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.teamId = teamId;
        this.playerId = playerId;
        this.timestamp = timestamp;
        this.period = period;
        this.matchId = matchId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public Integer getMatchId() { return matchId; }
    public void setMatchId(Integer matchId) { this.matchId = matchId; }
}
