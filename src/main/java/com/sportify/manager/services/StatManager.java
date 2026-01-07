package com.sportify.manager.services;

import com.sportify.manager.dao.StatDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatManager {

    public List<Statistique> calculateTeamPerformance(int teamId, String period) throws SQLException {
        // Récupération du DAO via la Factory
        StatDAO statDAO = AbstractFactory.getFactory().createStatDAO();

        // Récupération des données brutes
        List<SmallEvent> events = statDAO.getEventsByTeam(teamId, period);

        List<Statistique> results = new ArrayList<>();

        // 1. Calcul du total des buts
        long buts = events.stream().filter(e -> "GOAL".equalsIgnoreCase(e.getType())).count();
        results.add(new Statistique("Total Buts", (double) buts, period, "buts"));

        // 2. Calcul des cartons (discipline)
        long cartons = events.stream().filter(e -> e.getType().contains("CARD")).count();
        results.add(new Statistique("Fautes graves", (double) cartons, period, "cartons"));

        // 3. Exemple de Ratio (Buts par événement enregistré)
        if (!events.isEmpty()) {
            double ratio = (double) buts / events.size();
            results.add(new Statistique("Efficacité", ratio, period, "buts/action"));
        }

        return results;
    }
}