package com.sportify.manager.MatchManagement;

import java.time.LocalDateTime;

public class Match {
    private Integer id;

    private int typeSportId;               // IMPORTANT: lié à TypeSport
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getTypeSportId() { return typeSportId; }
    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }

    public LocalDateTime getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public String getReferee() { return referee; }
    public LocalDateTime getCompositionDeadline() { return compositionDeadline; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Integer getHomeScore() { return homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setScore(Integer homeScore, Integer awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}
