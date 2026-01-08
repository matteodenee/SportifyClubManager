package com.sportify.manager.MatchManagement;

import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.match.dao.MatchDAO;
import com.sportify.manager.match.dao.PostgresMatchDAO;
import com.sportify.manager.match.model.Match;
import com.sportify.manager.match.model.MatchStatus;
import com.sportify.manager.services.TypeSport;

import com.sportify.manager.services.TypeSportManager;

import java.sql.Connection;
import java.util.List;

public class MatchManager {

    private static MatchManager instance;

    private final MatchDAO matchDAO;
    private final TypeSportManager typeSportManager;

    private MatchManager() {
        PostgresUserDAO.getInstance();
        Connection connection = PostgresUserDAO.getConnection();

        this.matchDAO = new PostgresMatchDAO(connection);
        this.typeSportManager = new TypeSportManager();
    }

    public static MatchManager getInstance() {
        if (instance == null) instance = new MatchManager();
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

    // petit helper utile pour UI (optionnel)
    public TypeSport getTypeSport(int typeSportId) throws Exception {
        return typeSportManager.getTypeSportById(typeSportId);
    }

    private void validateMatch(Match m, boolean creating) throws Exception {
        if (m == null) throw new Exception("Match null");
        if (m.getTypeSportId() <= 0) throw new Exception("TypeSport invalide");
        if (m.getHomeTeamId() <= 0 || m.getAwayTeamId() <= 0) throw new Exception("Teams invalides");
        if (m.getHomeTeamId() == m.getAwayTeamId()) throw new Exception("Même équipe home/away");
        if (m.getDateTime() == null) throw new Exception("Date/heure manquante");
        if (m.getLocation() == null || m.getLocation().isBlank()) throw new Exception("Lieu manquant");

        // vérifie que le typeSport existe (sinon compo ne marchera pas)
        TypeSport ts = typeSportManager.getTypeSportById(m.getTypeSportId());
        if (ts == null) throw new Exception("TypeSport introuvable");

        // deadline logique
        if (m.getCompositionDeadline() != null && m.getCompositionDeadline().isAfter(m.getDateTime())) {
            // tu peux autoriser ou refuser, au choix — je refuse car deadline devrait être AVANT le match
            throw new Exception("Deadline de composition incohérente (après le match)");
        }
    }
}
