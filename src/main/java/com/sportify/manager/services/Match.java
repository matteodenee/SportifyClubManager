package com.sportify.manager.services;

import java.time.LocalDateTime;

public class Match {
    private Integer id;
    private int typeSportId;
    private int homeTeamId;
    private int awayTeamId;
    private LocalDateTime dateTime;
    private String location;
    private String referee;
    private LocalDateTime compositionDeadline;
    private MatchStatus status;
    private Integer homeScore;
    private Integer awayScore;

    public Match(Integer id, int typeSportId, int homeTeamId, int awayTeamId,
                 LocalDateTime dateTime, String location, String referee,
                 LocalDateTime compositionDeadline,
                 MatchStatus status, Integer homeScore, Integer awayScore) {
        this.id = id;
        this.typeSportId = typeSportId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.dateTime = dateTime;
        this.location = location;
        this.referee = referee;
        this.compositionDeadline = compositionDeadline;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    // --- GETTERS & SETTERS (Crucial pour MatchManager et DAO) ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getTypeSportId() { return typeSportId; }
    public void setTypeSportId(int typeSportId) { this.typeSportId = typeSportId; }

    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getReferee() { return referee; }
    public void setReferee(String referee) { this.referee = referee; }

    public LocalDateTime getCompositionDeadline() { return compositionDeadline; }
    public void setCompositionDeadline(LocalDateTime deadline) { this.compositionDeadline = deadline; }

    // C'EST CETTE MÉTHODE QUI RÉSOUT TON ERREUR :
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }

    // --- MÉTHODE MÉTIER ---
    public String determineResultForTeam(int teamId) {
        if (homeScore == null || awayScore == null) return "NUL";
        if (teamId == homeTeamId) {
            if (homeScore > awayScore) return "VICTOIRE";
            if (homeScore < awayScore) return "DEFAITE";
        } else if (teamId == awayTeamId) {
            if (awayScore > homeScore) return "VICTOIRE";
            if (awayScore < homeScore) return "DEFAITE";
        }
        return "NUL";
    }
}