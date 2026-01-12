package com.sportify.manager.persistence;

import com.sportify.manager.dao.*;

public abstract class AbstractFactory {
    private static AbstractFactory instance = null;


    static {
        if (instance == null) {
            try {
                instance = new PostgresFactory();
            } catch (Exception e) {
            }
        }
    }

    public static AbstractFactory getFactory() {
        return instance;
    }

    public static void setFactory(AbstractFactory factory) {
        instance = factory;
    }


    public abstract UserDAO createUserDAO();
    public abstract ClubDAO createClubDAO();
    public abstract StatDAO createStatDAO();
    public abstract LicenceDAO createLicenceDAO();
    public abstract TypeSportDAO createTypeSportDAO();
    public abstract TeamDAO createTeamDAO();
    public abstract TrainingDAO createTrainingDAO();
    public abstract MatchDAO createMatchDAO();
    public abstract CompositionDAO createCompositionDAO();
    public abstract MatchRequestDAO createMatchRequestDAO();
    public abstract EventDAO createEventDAO();
    public abstract ConversationDAO createConversationDAO();
    public abstract MessageDAO createMessageDAO();
    public abstract EquipmentTypeDAO createEquipmentTypeDAO();
    public abstract EquipmentDAO createEquipmentDAO();
    public abstract ReservationDAO createReservationDAO();
    public abstract SmallEventDAO createSmallEventDAO();

}
