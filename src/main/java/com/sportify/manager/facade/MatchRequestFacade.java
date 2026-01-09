package com.sportify.manager.facade;

import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.MatchRequestManager;

import java.util.List;

public class MatchRequestFacade {
    private static MatchRequestFacade instance;
    private final MatchRequestManager manager;

    private MatchRequestFacade() {
        this.manager = MatchRequestManager.getInstance();
    }

    public static MatchRequestFacade getInstance() {
        if (instance == null) {
            instance = new MatchRequestFacade();
        }
        return instance;
    }

    public MatchRequest createRequest(MatchRequest request) throws Exception {
        return manager.createRequest(request);
    }

    public List<MatchRequest> getPendingRequests() throws Exception {
        return manager.getPendingRequests();
    }

    public List<MatchRequest> getRequestsByClub(int clubId) throws Exception {
        return manager.getRequestsByClub(clubId);
    }

    public boolean approveRequest(int requestId) throws Exception {
        return manager.approveRequest(requestId);
    }

    public boolean rejectRequest(int requestId) throws Exception {
        return manager.rejectRequest(requestId);
    }
}
