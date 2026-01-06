package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.services.MembershipRequest;
import com.sportify.manager.services.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class DirectorDashboardFrame extends Application {
    private ClubController clubController;
    private User currentUser;
    private TableView<MembershipRequest> requestTable;

    public DirectorDashboardFrame(User user) {
        this.currentUser = user;
    }

    public void setClubController(ClubController controller) {
        this.clubController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f4f7f6;");

        // --- Header ---
        Label titleLabel = new Label("Tableau de bord Directeur");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label subLabel = new Label("Gestion des demandes d'adhésion pour vos clubs");

        // --- Table des demandes ---
        requestTable = new TableView<>();

        TableColumn<MembershipRequest, String> userCol = new TableColumn<>("Candidat");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<MembershipRequest, String> clubCol = new TableColumn<>("Club visé");
        clubCol.setCellValueFactory(new PropertyValueFactory<>("clubName"));

        TableColumn<MembershipRequest, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        requestTable.getColumns().addAll(userCol, clubCol, statusCol);
        requestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Boutons d'action ---
        Button btnApprove = new Button("Approuver l'adhésion");
        btnApprove.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnReject = new Button("Refuser");
        btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        btnApprove.setOnAction(e -> handleAction(true));
        btnReject.setOnAction(e -> handleAction(false));

        HBox buttonBox = new HBox(15, btnApprove, btnReject);

        layout.getChildren().addAll(titleLabel, subLabel, requestTable, buttonBox);

        Scene scene = new Scene(layout, 700, 500);
        primaryStage.setTitle("Sportify - Espace Direction");
        primaryStage.setScene(scene);

        refreshTable();
        primaryStage.show();
    }

    private void refreshTable() {
        try {
            List<MembershipRequest> requests = clubController.getPendingRequests();
            requestTable.setItems(FXCollections.observableArrayList(requests));
        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    private void handleAction(boolean approve) {
        MembershipRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection requise", "Veuillez sélectionner une demande dans la liste.");
            return;
        }

        try {
            if (approve) {
                clubController.approveRequest(selected.getRequestId());
                showInfo("Succès", "L'utilisateur " + selected.getUserName() + " a été ajouté au club.");
            } else {
                clubController.rejectRequest(selected.getRequestId());
                showInfo("Refusé", "La demande a été rejetée.");
            }
            refreshTable();
        } catch (SQLException e) {
            showError("Erreur lors du traitement", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}