package com.sportify.manager.services;

import com.sportify.manager.dao.CompositionDAO;
import com.sportify.manager.dao.MatchDAO;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.time.LocalDateTime;
import java.util.*;

public class CompositionManager {

    private static CompositionManager instance;

    private final CompositionDAO compositionDAO;
    private final MatchDAO matchDAO;
    private final TypeSportManager typeSportManager;

    private CompositionManager() {
        // Utilisation de l'AbstractFactory pour rester cohérent avec tes autres managers
        AbstractFactory factory = AbstractFactory.getFactory();

        this.compositionDAO = factory.createCompositionDAO();
        this.matchDAO = factory.createMatchDAO();
        this.typeSportManager = TypeSportManager.getInstance(); // CORRECT
    }

    public static CompositionManager getInstance() {
        if (instance == null) {
            instance = new CompositionManager();
        }
        return instance;
    }

    public boolean saveComposition(Composition composition) {
        if (composition == null || composition.getAssignments() == null) return false;

        try {
            // 1) Récupérer sport + deadline depuis le match via le MatchDAO
            int sportId = matchDAO.getTypeSportId(composition.getMatchId());
            LocalDateTime deadline = matchDAO.getCompositionDeadline(composition.getMatchId());

            // Vérification de la deadline
            if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
                return false;
            }

            TypeSport typeSport = typeSportManager.getTypeSportById(sportId);
            if (typeSport == null) return false;

            for (RoleAssignment a : composition.getAssignments()) {
                String playerId = a.getPlayerId();
                if (playerId == null || playerId.isBlank()) {
                    return false;
                }
                if (!PostgresUserDAO.getInstance().hasActiveLicenceForSport(playerId.trim(), sportId)) {
                    return false;
                }
            }

            if (!validateComposition(composition.getAssignments(), typeSport)) {
                return false;
            }

            return compositionDAO.saveComposition(
                    composition.getMatchId(),
                    composition.getTeamId(),
                    composition.getAssignments()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateComposition(List<RoleAssignment> assignments, TypeSport typeSport) {
        // A) Nombre exact de joueurs requis pour ce sport
        if (assignments.size() != typeSport.getNbJoueurs()) return false;

        // B) Joueurs uniques (pas deux fois le même joueur dans la compo)
        Set<String> players = new HashSet<>();
        for (RoleAssignment a : assignments) {
            if (a.getPlayerId() == null || a.getPlayerId().isBlank()) return false;
            if (!players.add(a.getPlayerId())) return false;
        }

        // C) Vérification des rôles (selon le template du sport)
        List<String> rolesTemplate = typeSport.getRoles();
        if (rolesTemplate == null || rolesTemplate.size() != typeSport.getNbJoueurs()) return false;

        Map<String, Integer> required = new HashMap<>();
        for (String r : rolesTemplate) {
            required.merge(normalize(r), 1, Integer::sum);
        }

        Map<String, Set<Integer>> provided = new HashMap<>();
        for (RoleAssignment a : assignments) {
            String role = normalize(a.getRole());
            if (!required.containsKey(role)) return false;
            if (a.getSlotIndex() <= 0) return false;

            provided.putIfAbsent(role, new HashSet<>());
            if (!provided.get(role).add(a.getSlotIndex())) return false;
        }

        // Vérifier que chaque index (1..N) est présent pour chaque rôle
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
