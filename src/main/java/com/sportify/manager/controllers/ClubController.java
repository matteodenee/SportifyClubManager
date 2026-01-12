package com.sportify.manager.controllers;

import com.sportify.manager.dao.PostgresClubDAO;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.MembershipRequest;
import com.sportify.manager.dao.ClubDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClubController {

    private ClubDAO clubDAO;

    public ClubController(Connection connection) {
        this.clubDAO = new PostgresClubDAO(connection);
    }

    public List<Club> getAllClubs() throws SQLException {
        return clubDAO.getAllClubs();
    }

    public List<Club> getClubsByManager(String managerId) throws SQLException {
        return clubDAO.getClubsByManager(managerId);
    }

    public Club createClub(int clubID, String name, String description, int sportId, String type, int maxCapacity, String managerId) throws SQLException {
        Club newClub = new Club(clubID, name, description, sportId, type, maxCapacity, managerId);

        clubDAO.addClub(newClub);
        return newClub;
    }


    public boolean addMemberToClub(int clubId, String userId, String roleInClub) throws SQLException {
        if (clubDAO.isMember(clubId, userId)) {
            throw new SQLException("L'utilisateur " + userId + " est déjà membre de ce club.");
        }

        int currentMembers = clubDAO.getCurrentMembers(clubId);
        int maxCapacity = clubDAO.getMaxCapacity(clubId);

        if (currentMembers >= maxCapacity) {
            throw new SQLException("Le club a atteint sa capacité maximale (" + maxCapacity + ").");
        }

        clubDAO.addMemberToClub(clubId, userId, roleInClub);
        return true;
    }


    public void requestToJoinClub(int clubId, String userId, String roleInClub) throws SQLException {
        if (clubDAO.isMember(clubId, userId)) {
            throw new SQLException("Vous êtes déjà membre de ce club !");
        }
        if (clubDAO.hasPendingRequest(clubId, userId)) {
            throw new SQLException("Vous avez déjà une demande en cours de traitement pour ce club.");
        }
        clubDAO.createMembershipRequest(clubId, userId, roleInClub);
    }


    public List<MembershipRequest> getPendingRequests() throws SQLException {
        return clubDAO.getPendingRequests();
    }


    public List<MembershipRequest> getRequestsForDirector(String directorId) throws SQLException {
        return clubDAO.getPendingRequestsByDirector(directorId);
    }


    public void approveRequest(int requestId) throws SQLException {
        MembershipRequest request = clubDAO.getRequestById(requestId);
        if (request == null) {
            throw new SQLException("Demande introuvable.");
        }

        int current = clubDAO.getCurrentMembers(request.getClubId());
        int max = clubDAO.getMaxCapacity(request.getClubId());

        if (current >= max) {
            throw new SQLException("Impossible d'approuver : le club '" + request.getClubName() + "' est complet.");
        }

        clubDAO.updateRequestStatus(requestId, "APPROVED");
        String roleInClub = request.getRoleInClub() == null ? "JOUEUR" : request.getRoleInClub();
        clubDAO.addMemberToClub(request.getClubId(), request.getUserId(), roleInClub);
        if ("COACH".equalsIgnoreCase(roleInClub)) {
            PostgresUserDAO.getInstance().updateUserRole(request.getUserId(), "COACH");
        }
    }


    public void rejectRequest(int requestId) throws SQLException {
        clubDAO.updateRequestStatus(requestId, "REJECTED");
    }



    public void updateClub(Club club) throws SQLException {
        clubDAO.updateClub(club);
    }

    public void deleteClub(int clubID) throws SQLException {
        clubDAO.deleteClub(clubID);
    }

    public Club getClubById(int clubID) throws SQLException {
        return clubDAO.getClubById(clubID);
    }
}
