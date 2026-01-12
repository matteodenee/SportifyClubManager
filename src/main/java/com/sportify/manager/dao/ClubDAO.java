package com.sportify.manager.dao;

import com.sportify.manager.services.Club;
import com.sportify.manager.services.MembershipRequest;
import java.sql.SQLException;
import java.util.List;

public abstract class ClubDAO {

    public abstract void addClub(Club club) throws SQLException;
    public abstract void updateClub(Club club) throws SQLException;
    public abstract void deleteClub(int clubID) throws SQLException;
    public abstract Club getClubById(int clubID) throws SQLException;
    public abstract List<Club> getAllClubs() throws SQLException;
    public abstract List<Club> getClubsByManager(String managerId) throws SQLException;

    public abstract void addMemberToClub(int clubId, String userId, String roleInClub) throws SQLException;
    public abstract int getCurrentMembers(int clubId) throws SQLException;
    public abstract int getMaxCapacity(int clubId) throws SQLException;
    public abstract boolean isMember(int clubId, String userId) throws SQLException;



    public abstract void createMembershipRequest(int clubId, String userId, String roleInClub) throws SQLException;
    public abstract boolean hasPendingRequest(int clubId, String userId) throws SQLException;


    public abstract List<MembershipRequest> getPendingRequests() throws SQLException;


    public abstract List<MembershipRequest> getPendingRequestsByDirector(String directorId) throws SQLException;

    public abstract void updateRequestStatus(int requestId, String status) throws SQLException;
    public abstract MembershipRequest getRequestById(int requestId) throws SQLException;
}
