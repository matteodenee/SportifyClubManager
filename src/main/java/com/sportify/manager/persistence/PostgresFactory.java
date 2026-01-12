package com.sportify.manager.persistence;

import com.sportify.manager.dao.*;
import java.sql.Connection;

public class PostgresFactory extends AbstractFactory {

    public PostgresFactory() {

    }

    @Override
    public UserDAO createUserDAO() {
        return PostgresUserDAO.getInstance();
    }

    @Override
    public ClubDAO createClubDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresClubDAO(connection);
    }

    @Override
    public StatDAO createStatDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresStatDAO(connection);
    }

    @Override
    public LicenceDAO createLicenceDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresLicenceDAO(connection);
    }

    @Override
    public TypeSportDAO createTypeSportDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresTypeSportDAO(connection);
    }

    @Override
    public TeamDAO createTeamDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresTeamDAO(connection);
    }

    @Override
    public TrainingDAO createTrainingDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresTrainingDAO(connection);
    }



    @Override
    public MatchDAO createMatchDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresMatchDAO(connection);
    }

    @Override
    public CompositionDAO createCompositionDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresCompositionDAO(connection);
    }

    @Override
    public MatchRequestDAO createMatchRequestDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresMatchRequestDAO(connection);
    }

    @Override
    public EventDAO createEventDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresEventDAO(connection);
    }

    @Override
    public ConversationDAO createConversationDAO() {
        PostgresUserDAO.getInstance();
        Connection connection = PostgresUserDAO.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Connexion DB indisponible pour ConversationDAO.");
        }
        return new PostgresConversationDAO(connection);
    }

    @Override
    public MessageDAO createMessageDAO() {
        PostgresUserDAO.getInstance();
        Connection connection = PostgresUserDAO.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Connexion DB indisponible pour MessageDAO.");
        }
        return new PostgresMessageDAO(connection);
    }

    @Override
    public EquipmentTypeDAO createEquipmentTypeDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresEquipmentTypeDAO(connection);
    }

    @Override
    public EquipmentDAO createEquipmentDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresEquipmentDAO(connection);
    }

    @Override
    public ReservationDAO createReservationDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresReservationDAO(connection);
    }

    @Override
    public SmallEventDAO createSmallEventDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresSmallEventDAO(connection);
    }



}
