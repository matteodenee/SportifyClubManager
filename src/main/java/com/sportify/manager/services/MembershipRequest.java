package com.sportify.manager.services;

public class MembershipRequest {
    private int requestId;
    private int clubId;
    private String userId;
    private String clubName; // Pour afficher "Club de Foot" au lieu de "ID: 1"
    private String userName; // Pour afficher "Paul" au lieu de "user1"
    private String status;

    public MembershipRequest(int requestId, int clubId, String userId, String clubName, String userName, String status) {
        this.requestId = requestId;
        this.clubId = clubId;
        this.userId = userId;
        this.clubName = clubName;
        this.userName = userName;
        this.status = status;
    }

    // Getters indispensables pour le PropertyValueFactory du tableau
    public int getRequestId() { return requestId; }
    public String getClubName() { return clubName; }
    public String getUserName() { return userName; }
    public String getStatus() { return status; }
    public int getClubId() { return clubId; }
    public String getUserId() { return userId; }
}