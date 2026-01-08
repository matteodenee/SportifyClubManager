package com.sportify.manager.CompositionManagement;

import compositionManagement.model.Composition;
import compositionManagement.model.RoleAssignment;
import compositionManagement.persist.CompositionDAO;
import compositionManagement.persist.PostgresCompositionDAO;

import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.TypeSportManager;
import com.sportify.manager.dao.PostgresUserDAO;

import matchManagement.persist.MatchDAO;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;

public class CompositionManager {

    private static CompositionManager instance;

    private final CompositionDAO compositionDAO;
    private final TypeSportManager typeSportManager;
    private final MatchDAO matchDAO;

    private CompositionManager() {
        // même pattern que TypeSportManager
        PostgresUserDAO.getInstance();
        Connection connection = PostgresUserDAO.getConnection();

        this.compositionDAO = new PostgresCompositionDAO(connection);
        this.typeSportManager = new TypeSportManager();
        this.matchDAO = MatchDAO.getInstance(); // singleton aussi
    }

    public static CompositionManager getInstance() {
        if (instance == null) {
            instance = new CompositionManager();
        }
        return instance;
    }

    public boolean saveComposition(Composition composition) {
        if (composition == null || composition.getAssignments() == null) return false;

        // 1) récupérer sport + deadline depuis le match
        int sportId = matchDAO.getTypeSportId(composition.getMatchId());
        LocalDateTime deadline = matchDAO.getCompositionDeadline(composition.getMatchId());

        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            return false;
        }

        // 2) récupérer TypeSport
        TypeSport typeSport;
        try {
            typeSport = typeSportManager.getTypeSportById(sportId);
        } catch (Exception e) {
            return false;
        }
        if (typeSport == null) return false;

        // 3) validation métier
        if (!validateComposition(composition.getAssignments(), typeSport)) {
            return false;
        }

        // 4) persistance (create / update)
        return compositionDAO.saveComposition(
                composition.getMatchId(),
                composition.getTeamId(),
                composition.getAssignments()
        );
    }

    private boolean validateComposition(List<RoleAssignment> assignments, TypeSport typeSport) {

        // A) nombre exact de joueurs
        if (assignments.size() != typeSport.getNbJoueurs()) return false;

        // B) joueurs uniques
        Set<String> players = new HashSet<>();
        for (RoleAssignment a : assignments) {
            if (a.getPlayerId() == null || a.getPlayerId().isBlank()) return false;
            if (!players.add(a.getPlayerId())) return false;
        }

        // C) template des rôles
        List<String> rolesTemplate = typeSport.getRoles();
        if (rolesTemplate == null || rolesTemplate.size() != typeSport.getNbJoueurs()) return false;

        // compter slots requis par rôle
        Map<String, Integer> required = new HashMap<>();
        for (String r : rolesTemplate) {
            required.merge(normalize(r), 1, Integer::sum);
        }

        // compter slots fournis
        Map<String, Set<Integer>> provided = new HashMap<>();
        for (RoleAssignment a : assignments) {
            String role = normalize(a.getRole());
            if (!required.containsKey(role)) return false;

            if (a.getSlotIndex() <= 0) return false;

            provided.putIfAbsent(role, new HashSet<>());
            if (!provided.get(role).add(a.getSlotIndex())) return false;
        }

        // vérifier slotIndex 1..N pour chaque rôle
        for (Map.Entry<String, Integer> e : required.entrySet()) {
            String role = e.getKey();
            int needed = e.getValue();
            Set<Integer> got = provided.getOrDefault(role, Collections.emptySet());
            for (int i = 1; i <= needed; i++) {
                if (!got.contains(i)) return false;
            }
        }

        return true;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
