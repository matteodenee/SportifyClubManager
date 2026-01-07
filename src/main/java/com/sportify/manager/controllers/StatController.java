package com.sportify.manager.controllers;

import com.sportify.manager.facade.StatFacade;
import com.sportify.manager.services.Statistique;
import java.sql.SQLException;
import java.util.List;

public class StatController {
    private StatFacade statFacade;

    public StatController() {
        this.statFacade = new StatFacade();
    }

    public List<Statistique> getStatsForTeam(int teamId, String period) {
        try {
            return statFacade.getTeamStats(teamId, period);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}