package com.sportify.manager.services;

import java.util.List;

/**
 * Modèle représentant la feuille de match (Composition d'une équipe).
 */
public class Composition {
    private final int matchId;
    private final int teamId;
    private final List<RoleAssignment> assignments;

    public Composition(int matchId, int teamId, List<RoleAssignment> assignments) {
        this.matchId = matchId;
        this.teamId = teamId;
        this.assignments = assignments;
    }

    public int getMatchId() { return matchId; }
    public int getTeamId() { return teamId; }
    public List<RoleAssignment> getAssignments() { return assignments; }
}