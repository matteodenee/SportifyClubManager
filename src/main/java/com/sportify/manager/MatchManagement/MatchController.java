package com.sportify.manager.MatchManagement;

import com.sportify.manager.match.facade.MatchFacade;
import com.sportify.manager.match.model.Match;

import java.util.List;

public class MatchController {

    private static MatchController instance;
    private final MatchFacade matchFacade;

    private MatchController() {
        this.matchFacade = MatchFacade.getInstance();
    }

    public static MatchController getInstance() {
        if (instance == null) instance = new MatchController();
        return instance;
    }

    public Match handleCreateMatch(Match m) {
        try {
            return matchFacade.createMatch(m);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean handleUpdateMatch(Match m) {
        try {
            matchFacade.updateMatch(m);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Match> handleGetAllMatches() {
        try {
            return matchFacade.getAllMatches();
        } catch (Exception e) {
            return null;
        }
    }

    public Match handleGetMatchById(int id) {
        try {
            return matchFacade.getMatchById(id);
        } catch (Exception e) {
            return null;
        }
    }
}
