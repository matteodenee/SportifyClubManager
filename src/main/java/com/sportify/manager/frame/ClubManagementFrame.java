package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.services.Club;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class ClubManagementFrame extends Application {
    private ClubController clubController;

    private TextField clubNameField;
    private TextField clubDescriptionField;
    private TextField clubTypeField;
    private TextField meetingScheduleField;
    private TextField maxCapacityField;
    private TextField memberIdField;
    private Label messageLabel;
    private TableView<Club> clubTable;

    private int currentClubId = 0;

    public void setClubController(ClubController controller) {
        this.clubController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        if (clubController == null) {
            clubController = new ClubController(null);
        }

        // --- SECTION FORMULAIRE CLUB ---
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        clubNameField = new TextField();
        clubDescriptionField = new TextField();
        clubTypeField = new TextField();
        meetingScheduleField = new TextField();
        maxCapacityField = new TextField();
        messageLabel = new Label();

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(clubNameField, 1, 0);
        grid.add(new Label("Description :"), 0, 1);
        grid.add(clubDescriptionField, 1, 1);
        grid.add(new Label("Type :"), 0, 2);
        grid.add(clubTypeField, 1, 2);
        grid.add(new Label("Horaire :"), 0, 3);
        grid.add(meetingScheduleField, 1, 3);
        grid.add(new Label("Capacité :"), 0, 4);
        grid.add(maxCapacityField, 1, 4);

        Button addClubButton = new Button("Ajouter Nouveau");
        Button updateButton = new Button("Modifier Sélection");
        Button deleteButton = new Button("Supprimer Sélection");
        Button clearButton = new Button("Vider Champs");

        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

        grid.add(updateButton, 0, 5);
        grid.add(addClubButton, 1, 5);
        grid.add(deleteButton, 0, 6);
        grid.add(clearButton, 1, 6);

        // --- SECTION GESTION DES MEMBRES ---
        VBox memberSection = new VBox(10);
        memberSection.setPadding(new Insets(10, 0, 0, 0));
        memberSection.setStyle("-fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");

        Label memberTitle = new Label("Gestion des Membres");
        memberTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        memberIdField = new TextField();
        memberIdField.setPromptText("ID Utilisateur (ex: user1)");

        Button addMemberButton = new Button("Inscrire au Club");
        addMemberButton.setMaxWidth(Double.MAX_VALUE);
        addMemberButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        memberSection.getChildren().addAll(memberTitle, memberIdField, addMemberButton);
        grid.add(memberSection, 0, 8, 2, 1);

        grid.add(messageLabel, 0, 9, 2, 1);

        // --- SECTION TABLEAU (MIS À JOUR AVEC COLONNE MEMBRES) ---
        clubTable = new TableView<>();
        TableColumn<Club, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Club, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Nouvelle colonne pour le nombre actuel
        TableColumn<Club, Integer> currentCountCol = new TableColumn<>("Membres");
        currentCountCol.setCellValueFactory(new PropertyValueFactory<>("currentMemberCount"));

        TableColumn<Club, Integer> capCol = new TableColumn<>("Max");
        capCol.setCellValueFactory(new PropertyValueFactory<>("maxCapacity"));

        clubTable.getColumns().addAll(nameCol, typeCol, currentCountCol, capCol);
        clubTable.setMinWidth(450);

        clubTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentClubId = newSelection.getClubID();
                clubNameField.setText(newSelection.getName());
                clubDescriptionField.setText(newSelection.getDescription());
                clubTypeField.setText(newSelection.getType());
                meetingScheduleField.setText(newSelection.getMeetingSchedule());
                maxCapacityField.setText(String.valueOf(newSelection.getMaxCapacity()));
                showMessage("Sélection : " + newSelection.getName() + " (" + newSelection.getCurrentMemberCount() + "/" + newSelection.getMaxCapacity() + ")");
            }
        });

        // --- LOGIQUE DES BOUTONS ---

        addClubButton.setOnAction(event -> {
            try {
                clubController.createClub(0, clubNameField.getText(), clubDescriptionField.getText(),
                        clubTypeField.getText(), meetingScheduleField.getText(),
                        Integer.parseInt(maxCapacityField.getText()));
                showMessage("Club ajouté !");
                refreshClubList();
                clearFields();
            } catch (Exception e) {
                showMessage("Erreur : " + e.getMessage());
            }
        });

        updateButton.setOnAction(event -> {
            if (currentClubId == 0) {
                showMessage("Sélectionnez d'abord un club !");
                return;
            }
            try {
                Club updatedClub = new Club(currentClubId, clubNameField.getText(), clubDescriptionField.getText(),
                        clubTypeField.getText(), meetingScheduleField.getText(),
                        Integer.parseInt(maxCapacityField.getText()));
                clubController.updateClub(updatedClub);
                showMessage("Club mis à jour !");
                refreshClubList();
            } catch (Exception e) {
                showMessage("Erreur modification : " + e.getMessage());
            }
        });

        deleteButton.setOnAction(event -> {
            if (currentClubId == 0) {
                showMessage("Sélectionnez un club à supprimer !");
                return;
            }
            try {
                clubController.deleteClub(currentClubId);
                showMessage("Club supprimé !");
                refreshClubList();
                clearFields();
                currentClubId = 0;
            } catch (SQLException e) {
                showMessage("Erreur suppression : " + e.getMessage());
            }
        });

        // LOGIQUE ADD MEMBER (UC 6)
        addMemberButton.setOnAction(event -> {
            if (currentClubId == 0) {
                showMessage("Sélectionnez un club dans le tableau !");
                return;
            }
            String userId = memberIdField.getText().trim();
            if (userId.isEmpty()) {
                showMessage("Entrez un ID utilisateur.");
                return;
            }
            try {
                boolean success = clubController.addMemberToClub(currentClubId, userId);
                if (success) {
                    showMessage("Utilisateur " + userId + " ajouté !");
                    memberIdField.clear();
                    refreshClubList(); // INDISPENSABLE pour mettre à jour le compteur dans le tableau
                }
            } catch (SQLException e) {
                showMessage("Erreur : " + e.getMessage());
            }
        });

        clearButton.setOnAction(event -> clearFields());

        HBox mainLayout = new HBox(20, grid, clubTable);
        mainLayout.setPadding(new Insets(15));

        Scene scene = new Scene(mainLayout, 1000, 600);
        primaryStage.setTitle("Sportify Manager - Administration");
        primaryStage.setScene(scene);

        refreshClubList();
        primaryStage.show();
    }

    private void refreshClubList() {
        if (clubController != null) {
            try {
                List<Club> clubs = clubController.getAllClubs();
                clubTable.setItems(FXCollections.observableArrayList(clubs));
            } catch (SQLException e) {
                showMessage("Erreur chargement : " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        clubNameField.clear();
        clubDescriptionField.clear();
        clubTypeField.clear();
        meetingScheduleField.clear();
        maxCapacityField.clear();
        memberIdField.clear();
        currentClubId = 0;
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}