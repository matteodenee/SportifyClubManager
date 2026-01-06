package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class MemberDashboardFrame extends Application {
    private ClubController clubController;
    private User currentUser;
    private TableView<Club> clubTable;

    public MemberDashboardFrame(User user) {
        this.currentUser = user;
    }

    public void setClubController(ClubController controller) {
        this.clubController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Espace Membre - " + currentUser.getName());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Tableau simplifié (Lecture seule)
        clubTable = new TableView<>();

        TableColumn<Club, String> nameCol = new TableColumn<>("Club");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Club, String> typeCol = new TableColumn<>("Discipline");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Club, Integer> countCol = new TableColumn<>("Membres");
        countCol.setCellValueFactory(new PropertyValueFactory<>("currentMemberCount"));

        clubTable.getColumns().addAll(nameCol, typeCol, countCol);
        clubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button joinButton = new Button("Demander à rejoindre");
        joinButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        joinButton.setMaxWidth(Double.MAX_VALUE);

        joinButton.setOnAction(e -> {
            Club selected = clubTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleJoinRequest(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez choisir un club dans la liste.");
            }
        });

        layout.getChildren().addAll(welcomeLabel, new Label("Clubs disponibles :"), clubTable, joinButton);

        Scene scene = new Scene(layout, 650, 450);
        primaryStage.setTitle("Sportify - Mon Espace");
        primaryStage.setScene(scene);

        refreshList();
        primaryStage.show();
    }

    private void refreshList() {
        try {
            List<Club> clubs = clubController.getAllClubs();
            clubTable.setItems(FXCollections.observableArrayList(clubs));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les clubs : " + e.getMessage());
        }
    }

    private void handleJoinRequest(Club club) {
        try {
            // Appel à la logique métier du contrôleur
            clubController.requestToJoinClub(club.getClubID(), currentUser.getId());

            showAlert(Alert.AlertType.INFORMATION, "Demande envoyée",
                    "Votre demande pour rejoindre le club '" + club.getName() + "' a été enregistrée avec succès !");

        } catch (SQLException e) {
            // Affichage des erreurs métier (déjà membre, demande déjà en cours, etc.)
            showAlert(Alert.AlertType.ERROR, "Action impossible", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}