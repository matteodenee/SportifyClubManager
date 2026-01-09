package com.sportify.manager.controllers;

import com.sportify.manager.facade.MatchRequestFacade;
import com.sportify.manager.services.MatchRequest;

import java.util.List;

public class MatchRequestController {
    private static MatchRequestController instance;
    private final MatchRequestFacade facade;
    private String lastError;

    private MatchRequestController() {
        this.facade = MatchRequestFacade.getInstance();
    }

    public static MatchRequestController getInstance() {
        if (instance == null) {
            instance = new MatchRequestController();
        }
        return instance;
    }

    public MatchRequest handleCreateRequest(MatchRequest request) {
        try {
            lastError = null;
            return facade.createRequest(request);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public List<MatchRequest> handleGetPendingRequests() {
        try {
            lastError = null;
            return facade.getPendingRequests();
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public List<MatchRequest> handleGetRequestsByClub(int clubId) {
        try {
            lastError = null;
            return facade.getRequestsByClub(clubId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public boolean handleApproveRequest(int requestId) {
        try {
            lastError = null;
            return facade.approveRequest(requestId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean handleRejectRequest(int requestId) {
        try {
            lastError = null;
            return facade.rejectRequest(requestId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public String getLastError() {
        return lastError;
    }
}
