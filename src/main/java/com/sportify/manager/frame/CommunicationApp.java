package com.sportify.manager.frame;

import com.sportify.manager.services.Session;
import com.sportify.manager.services.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class CommunicationApp extends Application {
    @Override
    public void start(Stage stage) {
        if (!Session.isLoggedIn()) {
            Session.setCurrentUser(new User("u1", "pwd", "Matteo", "matteo@example.com", "MEMBER"));
        }

        new CommunicationFrame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
