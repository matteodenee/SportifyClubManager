package com.sportify.manager.services;

import com.sportify.manager.dao.MatchRequestDAO;
import com.sportify.manager.persistence.AbstractFactory;

import java.util.List;

public class MatchRequestManager {
    private static MatchRequestManager instance;
    private final MatchRequestDAO requestDAO;
    private final MatchManager matchManager;

    private MatchRequestManager() {
        AbstractFactory factory = AbstractFactory.getFactory();
        this.requestDAO = factory.createMatchRequestDAO();
        this.matchManager = MatchManager.getInstance();
    }

    public static MatchRequestManager getInstance() {
        if (instance == null) {
            instance = new MatchRequestManager();
        }
        return instance;
    }

    public MatchRequest createRequest(MatchRequest request) throws Exception {
        validateRequest(request);
        request.setStatus(MatchRequestStatus.PENDING);
        return requestDAO.create(request);
    }

    public List<MatchRequest> getPendingRequests() throws Exception {
        return requestDAO.getPending();
    }

    public List<MatchRequest> getRequestsByClub(int clubId) throws Exception {
        return requestDAO.getByClub(clubId);
    }

    public boolean approveRequest(int requestId) throws Exception {
        MatchRequest req = requestDAO.getById(requestId);
        if (req == null) return false;

        Match match = new Match(
                null,
                req.getTypeSportId(),
                req.getHomeTeamId(),
                req.getAwayTeamId(),
                req.getRequestedDateTime(),
                req.getLocation(),
                req.getReferee(),
                null,
                MatchStatus.SCHEDULED,
                null,
                null
        );

        Match created = matchManager.createMatch(match);
        if (created == null || created.getId() == null) return false;

        requestDAO.updateStatus(req.getId(), MatchRequestStatus.APPROVED, created.getId());
        return true;
    }

    public boolean rejectRequest(int requestId) throws Exception {
        MatchRequest req = requestDAO.getById(requestId);
        if (req == null) return false;
        requestDAO.updateStatus(req.getId(), MatchRequestStatus.REJECTED, null);
        return true;
    }

    private void validateRequest(MatchRequest request) throws Exception {
        if (request == null) throw new Exception("Demande vide");
        if (request.getTypeSportId() <= 0) throw new Exception("Sport invalide");
        if (request.getHomeTeamId() <= 0 || request.getAwayTeamId() <= 0) throw new Exception("Teams invalides");
        if (request.getHomeTeamId() == request.getAwayTeamId()) throw new Exception("Même équipe home/away");
        if (request.getRequestedDateTime() == null) throw new Exception("Date/heure manquante");
        if (request.getRequestedBy() == null || request.getRequestedBy().isBlank()) {
            throw new Exception("Demandeur manquant");
        }
    }
}
