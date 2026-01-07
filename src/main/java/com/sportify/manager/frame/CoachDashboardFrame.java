package com.sportify.manager.frame;

import com.sportify.manager.services.User;
import com.sportify.manager.dao.PostgresUserDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CoachDashboardFrame extends Application {
    private User currentCoach;
    private int coachClubId; // Stocke l'ID du club récupéré en BDD

    public CoachDashboardFrame(User user) {
        this.currentCoach = user;
        // Récupération dynamique du club associé à ce coach
        this.coachClubId = PostgresUserDAO.getInstance().getClubIdByCoach(user.getId());
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f7f6;");

        // --- SIDEBAR ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("MENU COACH");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnTeam = createMenuButton("👥 Team Manage");
        Button btnTraining = createMenuButton("🏋️ Training Manage");
        Button btnMatch = createMenuButton("⚽ Match Manage");
        Button btnStats = createMenuButton("📊 Stat Manage");

        btnStats.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnTeam, btnTraining, btnMatch, btnStats);

        // --- MAIN CONTENT AREA ---
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));

        Label welcomeLabel = new Label("Tableau de bord : " + currentCoach.getName());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Affichage de l'ID du club (utile pour vérifier que le lien BDD fonctionne)
        Label clubInfo = new Label("Club assigné ID : " + (coachClubId != -1 ? coachClubId : "Aucun club trouvé"));
        clubInfo.setStyle("-fx-text-fill: #7f8c8d;");

        StackPane displayArea = new StackPane();
        displayArea.setPrefHeight(400);
        displayArea.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label placeholderText = new Label("Sélectionnez une fonctionnalité dans le menu de gauche.");
        displayArea.getChildren().add(placeholderText);

        // --- ACTIONS ---

        // Utilisation de l'ID dynamique pour les stats
        btnStats.setOnAction(e -> {
            if (coachClubId != -1) {
                StatFrame statFrame = new StatFrame();
                statFrame.show(coachClubId); // ON PASSE L'ID DYNAMIQUE ICI
            } else {
                placeholderText.setText("Erreur : Aucun club n'est associé à votre compte coach.");
            }
        });

        btnTeam.setOnAction(e -> placeholderText.setText("Module 'Equipe' - Club ID: " + coachClubId));
        btnTraining.setOnAction(e -> placeholderText.setText("Module 'Entrainement' - Club ID: " + coachClubId));
        btnMatch.setOnAction(e -> placeholderText.setText("Module 'Match' - Club ID: " + coachClubId));

        mainContent.getChildren().addAll(welcomeLabel, clubInfo, displayArea);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Sportify - Coach Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> {
            if (!text.contains("Stat")) btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        });
        btn.setOnMouseExited(e -> {
            if (!text.contains("Stat")) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT;");
            } else {
                btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;");
            }
        });

        return btn;
    }
}