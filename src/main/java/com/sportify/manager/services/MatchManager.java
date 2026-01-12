package com.sportify.manager.services;

import com.sportify.manager.dao.MatchDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.util.List;


public class MatchManager {

    private static MatchManager instance;
    private final MatchDAO matchDAO;
    private final TypeSportManager typeSportManager;

    private MatchManager() {

        if (AbstractFactory.getFactory() == null) {
            throw new RuntimeException("AbstractFactory n'est pas initialisée !");
        }
        this.matchDAO = AbstractFactory.getFactory().createMatchDAO();
        this.typeSportManager = TypeSportManager.getInstance();
    }

    public static synchronized MatchManager getInstance() {
        if (instance == null) {
            instance = new MatchManager();
        }
        return instance;
    }

    public Match createMatch(Match m) throws Exception {
        validateMatch(m, true);
        m.setStatus(MatchStatus.SCHEDULED);
        return matchDAO.create(m);
    }

    public void updateMatch(Match m) throws Exception {
        if (m.getId() == null) throw new Exception("Match sans id");
        validateMatch(m, false);
        matchDAO.update(m);
    }

    public Match getMatchById(int id) throws Exception {
        return matchDAO.getById(id);
    }

    public List<Match> getAllMatches() throws Exception {
        return matchDAO.getAll();
    }

    public List<Match> getMatchesByClub(int clubId) throws Exception {
        if (clubId <= 0) throw new Exception("Club invalide");
        return matchDAO.getByClub(clubId);
    }

    public TypeSport getTypeSport(int typeSportId) throws Exception {
        return typeSportManager.getTypeSportById(typeSportId);
    }

    private void validateMatch(Match m, boolean creating) throws Exception {
        if (m == null) throw new Exception("Match null");
        if (m.getTypeSportId() <= 0) throw new Exception("TypeSport invalide");


        TypeSport ts = typeSportManager.getTypeSportById(m.getTypeSportId());
        if (ts == null) throw new Exception("TypeSport introuvable");

        if (m.getHomeTeamId() <= 0 || m.getAwayTeamId() <= 0) throw new Exception("Teams invalides");
        if (m.getHomeTeamId() == m.getAwayTeamId()) throw new Exception("Même équipe home/away");
        if (m.getDateTime() == null) throw new Exception("Date/heure manquante");

        if (m.getCompositionDeadline() != null && m.getCompositionDeadline().isAfter(m.getDateTime())) {
            throw new Exception("Deadline de composition incohérente (après le match)");
        }
    }
}
