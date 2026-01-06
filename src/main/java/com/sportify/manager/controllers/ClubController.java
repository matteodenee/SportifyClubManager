package com.sportify.manager.controllers;

import com.sportify.manager.dao.PostgresClubDAO;
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

    public Club createClub(int clubID, String name, String description, String type, String meetingSchedule, int maxCapacity) throws SQLException {
        Club newClub = new Club(clubID, name, description, type, meetingSchedule, maxCapacity);
        clubDAO.addClub(newClub);
        return newClub;
    }

    /**
     * Inscription DIRECTE par l'Admin (UC 6)
     */
    public boolean addMemberToClub(int clubId, String userId) throws SQLException {
        if (clubDAO.isMember(clubId, userId)) {
            throw new SQLException("L'utilisateur " + userId + " est déjà membre de ce club.");
        }

        int currentMembers = clubDAO.getCurrentMembers(clubId);
        int maxCapacity = clubDAO.getMaxCapacity(clubId);

        if (currentMembers >= maxCapacity) {
            throw new SQLException("Le club a atteint sa capacité maximale (" + maxCapacity + ").");
        }

        clubDAO.addMemberToClub(clubId, userId);
        return true;
    }

    /**
     * Demande d'adhésion par le MEMBRE (Nouvelle logique UC 7)
     */
    public void requestToJoinClub(int clubId, String userId) throws SQLException {
        if (clubDAO.isMember(clubId, userId)) {
            throw new SQLException("Vous êtes déjà membre de ce club !");
        }
        if (clubDAO.hasPendingRequest(clubId, userId)) {
            throw new SQLException("Vous avez déjà une demande en cours de traitement pour ce club.");
        }
        clubDAO.createMembershipRequest(clubId, userId);
    }

    // --- NOUVELLES MÉTHODES POUR LE DIRECTEUR (UC 8) ---

    /**
     * Récupère toutes les demandes en attente pour affichage dans le tableau
     */
    public List<MembershipRequest> getPendingRequests() throws SQLException {
        return clubDAO.getPendingRequests();
    }

    /**
     * Approuve une demande et inscrit automatiquement le membre au club
     */
    public void approveRequest(int requestId) throws SQLException {
        // 1. Récupérer les infos de la demande
        MembershipRequest request = clubDAO.getRequestById(requestId);
        if (request == null) {
            throw new SQLException("Demande introuvable.");
        }

        // 2. Vérifier la capacité du club (Sécurité métier)
        int current = clubDAO.getCurrentMembers(request.getClubId());
        int max = clubDAO.getMaxCapacity(request.getClubId());

        if (current >= max) {
            throw new SQLException("Impossible d'approuver : le club '" + request.getClubName() + "' est complet.");
        }

        // 3. Mettre à jour le statut de la demande
        clubDAO.updateRequestStatus(requestId, "APPROVED");

        // 4. Ajouter l'utilisateur à la table des membres
        clubDAO.addMemberToClub(request.getClubId(), request.getUserId());
    }

    /**
     * Refuse une demande sans inscrire le membre
     */
    public void rejectRequest(int requestId) throws SQLException {
        clubDAO.updateRequestStatus(requestId, "REJECTED");
    }

    // --- AUTRES MÉTHODES ---

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