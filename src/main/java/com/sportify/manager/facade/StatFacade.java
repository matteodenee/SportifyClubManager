package com.sportify.manager.facade;

import com.sportify.manager.services.StatManager;
import com.sportify.manager.services.Statistique;
import java.sql.SQLException;
import java.util.List;

public class StatFacade {
    private StatManager statManager;

    public StatFacade() {
        this.statManager = new StatManager();
    }

    public List<Statistique> getTeamStats(int teamId, String period) throws SQLException {
        return statManager.calculateTeamPerformance(teamId, period);
    }
}