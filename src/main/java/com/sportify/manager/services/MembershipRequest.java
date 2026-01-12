package com.sportify.manager.services;

public class MembershipRequest {
    private int requestId;
    private int clubId;
    private String userId;
    private String clubName;
    private String userName;
    private String status;
    private String roleInClub;

    public MembershipRequest(int requestId, int clubId, String userId, String clubName, String userName, String status, String roleInClub) {
        this.requestId = requestId;
        this.clubId = clubId;
        this.userId = userId;
        this.clubName = clubName;
        this.userName = userName;
        this.status = status;
        this.roleInClub = roleInClub;
    }


    public int getRequestId() { return requestId; }
    public String getClubName() { return clubName; }
    public String getUserName() { return userName; }
    public String getStatus() { return status; }
    public int getClubId() { return clubId; }
    public String getUserId() { return userId; }
    public String getRoleInClub() { return roleInClub; }
}
