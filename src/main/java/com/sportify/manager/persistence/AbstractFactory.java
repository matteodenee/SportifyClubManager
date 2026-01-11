package com.sportify.manager.persistence;

import com.sportify.manager.dao.*;

public abstract class AbstractFactory {
    private static AbstractFactory instance = null;

    // --- BLOC D'AUTO-INITIALISATION ---
    // Ce code s'exécute AUTOMATIQUEMENT dès que AbstractFactory est référencé.
    static {
        if (instance == null) {
            try {
                // On injecte la version Postgres par défaut
                instance = new PostgresFactory();
                System.out.println("[SYSTÈME] AbstractFactory : Auto-initialisation avec PostgresFactory réussie.");
            } catch (Exception e) {
                System.err.println("[ERREUR] Impossible d'auto-initialiser la Factory : " + e.getMessage());
            }
        }
    }

    public static AbstractFactory getFactory() {
        return instance;
    }

    public static void setFactory(AbstractFactory factory) {
        instance = factory;
    }

    // Méthodes abstraites existantes
    public abstract UserDAO createUserDAO();
    public abstract ClubDAO createClubDAO();
    public abstract StatDAO createStatDAO();
    public abstract LicenceDAO createLicenceDAO();
    public abstract TypeSportDAO createTypeSportDAO();
    public abstract TeamDAO createTeamDAO();
    public abstract TrainingDAO createTrainingDAO();

    // Nouvelles méthodes pour les matchs et compositions
    public abstract MatchDAO createMatchDAO();
    public abstract CompositionDAO createCompositionDAO();
    public abstract MatchRequestDAO createMatchRequestDAO();
    public abstract EventDAO createEventDAO();
    public abstract ConversationDAO createConversationDAO();
    public abstract MessageDAO createMessageDAO();
    public abstract EquipmentTypeDAO createEquipmentTypeDAO();
    public abstract EquipmentDAO createEquipmentDAO();
    public abstract ReservationDAO createReservationDAO();

}
