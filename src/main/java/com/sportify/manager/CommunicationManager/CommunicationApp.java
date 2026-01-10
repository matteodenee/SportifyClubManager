package com.sportify.manager.CommunicationManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class CommunicationApp extends Application {
    @Override
    public void start(Stage stage) {
        // Normalement, Session.currentUser est déjà défini par le Login de l'app
        // Pour test local seulement:
        if (!Session.isLoggedIn()) {
            Session.setCurrentUser(new User("u1", "Matteo"));
        }

        new CommunicationFrame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
