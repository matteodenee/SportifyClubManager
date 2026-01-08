package com.sportify.manager.MatchManagement;

import com.sportify.manager.match.model.Match;
import com.sportify.manager.match.services.MatchManager;

import java.util.List;

public class MatchFacade {

    private static MatchFacade instance;
    private final MatchManager matchManager;

    private MatchFacade() {
        this.matchManager = MatchManager.getInstance();
    }

    public static MatchFacade getInstance() {
        if (instance == null) instance = new MatchFacade();
        return instance;
    }

    public Match createMatch(Match m) throws Exception {
        return matchManager.createMatch(m);
    }

    public void updateMatch(Match m) throws Exception {
        matchManager.updateMatch(m);
    }

    public List<Match> getAllMatches() throws Exception {
        return matchManager.getAllMatches();
    }

    public Match getMatchById(int id) throws Exception {
        return matchManager.getMatchById(id);
    }
}
