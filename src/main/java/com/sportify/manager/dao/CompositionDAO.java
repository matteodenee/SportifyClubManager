package com.sportify.manager.dao;

import java.util.List;
import com.sportify.manager.services.RoleAssignment;

/**
 * Interface de persistance pour la gestion des feuilles de match.
 */
public interface CompositionDAO {

    /**
     * Enregistre ou met à jour la composition complète d'une équipe pour un match.
     */
    boolean saveComposition(int matchId, int teamId, List<RoleAssignment> assignments);

    /**
     * Vérifie si une composition existe déjà pour ce match et cette équipe.
     */
    boolean hasAnyComposition(int matchId, int teamId);
}