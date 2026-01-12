package com.sportify.manager.controllers;

import com.sportify.manager.facade.MatchFacade;
import com.sportify.manager.services.Match;

import java.util.List;

public class MatchController {

    private static MatchController instance;
    private final MatchFacade matchFacade;
    private String lastError;

    private MatchController() {

        this.matchFacade = MatchFacade.getInstance();
    }

    public static MatchController getInstance() {
        if (instance == null) {
            instance = new MatchController();
        }
        return instance;
    }

    public Match handleCreateMatch(Match m) {
        try {
            lastError = null;
            return matchFacade.createMatch(m);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public boolean handleUpdateMatch(Match m) {
        try {
            lastError = null;
            matchFacade.updateMatch(m);
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public List<Match> handleGetAllMatches() {
        try {
            lastError = null;
            return matchFacade.getAllMatches();
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public List<Match> handleGetMatchesByClub(int clubId) {
        try {
            lastError = null;
            return matchFacade.getMatchesByClub(clubId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public Match handleGetMatchById(int id) {
        try {
            lastError = null;
            return matchFacade.getMatchById(id);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public String getLastError() {
        return lastError;
    }
}
