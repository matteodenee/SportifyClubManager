package com.sportify.manager.services;

import java.time.LocalDateTime;

public class MatchRequest {
    private Integer id;
    private int requesterClubId;
    private int opponentClubId;
    private int homeTeamId;
    private int awayTeamId;
    private int typeSportId;
    private LocalDateTime requestedDateTime;
    private String location;
    private String referee;
    private String requestedBy;
    private MatchRequestStatus status;
    private LocalDateTime requestDate;
    private Integer matchId;

    public MatchRequest(Integer id,
                        int requesterClubId,
                        int opponentClubId,
                        int homeTeamId,
                        int awayTeamId,
                        int typeSportId,
                        LocalDateTime requestedDateTime,
                        String location,
                        String referee,
                        String requestedBy,
                        MatchRequestStatus status,
                        LocalDateTime requestDate,
                        Integer matchId) {
        this.id = id;
        this.requesterClubId = requesterClubId;
        this.opponentClubId = opponentClubId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.typeSportId = typeSportId;
        this.requestedDateTime = requestedDateTime;
        this.location = location;
        this.referee = referee;
        this.requestedBy = requestedBy;
        this.status = status;
        this.requestDate = requestDate;
        this.matchId = matchId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getRequesterClubId() { return requesterClubId; }
    public int getOpponentClubId() { return opponentClubId; }
    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }
    public int getTypeSportId() { return typeSportId; }
    public LocalDateTime getRequestedDateTime() { return requestedDateTime; }
    public String getLocation() { return location; }
    public String getReferee() { return referee; }
    public String getRequestedBy() { return requestedBy; }
    public MatchRequestStatus getStatus() { return status; }
    public void setStatus(MatchRequestStatus status) { this.status = status; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public Integer getMatchId() { return matchId; }
    public void setMatchId(Integer matchId) { this.matchId = matchId; }
}
