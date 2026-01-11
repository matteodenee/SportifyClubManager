package com.sportify.manager.dao;

import com.sportify.manager.services.Club;
import com.sportify.manager.services.MembershipRequest;
import java.sql.SQLException;
import java.util.List;

public abstract class ClubDAO {
    // --- Gestion des Clubs ---
    public abstract void addClub(Club club) throws SQLException;
    public abstract void updateClub(Club club) throws SQLException;
    public abstract void deleteClub(int clubID) throws SQLException;
    public abstract Club getClubById(int clubID) throws SQLException;
    public abstract List<Club> getAllClubs() throws SQLException;
    public abstract List<Club> getClubsByManager(String managerId) throws SQLException;

    // --- Gestion Directe des Membres (Action Admin/Système) ---
    public abstract void addMemberToClub(int clubId, String userId) throws SQLException;
    public abstract int getCurrentMembers(int clubId) throws SQLException;
    public abstract int getMaxCapacity(int clubId) throws SQLException;
    public abstract boolean isMember(int clubId, String userId) throws SQLException;

    // --- Gestion des Demandes d'adhésion (UC 7 & 8) ---

    public abstract void createMembershipRequest(int clubId, String userId) throws SQLException;
    public abstract boolean hasPendingRequest(int clubId, String userId) throws SQLException;

    /** Récupère toutes les demandes (Admin) */
    public abstract List<MembershipRequest> getPendingRequests() throws SQLException;

    /** * NOUVEAU : Récupère uniquement les demandes pour les clubs
     * gérés par un directeur spécifique.
     */
    public abstract List<MembershipRequest> getPendingRequestsByDirector(String directorId) throws SQLException;

    public abstract void updateRequestStatus(int requestId, String status) throws SQLException;
    public abstract MembershipRequest getRequestById(int requestId) throws SQLException;
}
