package com.sportify.manager.facade;

import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchManager;
import java.util.List;

public class MatchFacade {

    private static MatchFacade instance;
    private final MatchManager matchManager;

    private MatchFacade() {
        // On récupère l'instance du manager située dans ton dossier services
        this.matchManager = MatchManager.getInstance();
    }

    public static MatchFacade getInstance() {
        if (instance == null) {
            instance = new MatchFacade();
        }
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

    public List<Match> getMatchesByClub(int clubId) throws Exception {
        return matchManager.getMatchesByClub(clubId);
    }

    public Match getMatchById(int id) throws Exception {
        return matchManager.getMatchById(id);
    }
}
