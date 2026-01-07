package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.services.Club;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class ClubManagementFrame extends Application {
    private ClubController clubController;

    private TextField clubNameField, clubDescriptionField, clubTypeField, meetingScheduleField, maxCapacityField, memberIdField;
    private Label messageLabel;
    private TableView<Club> clubTable;
    private int currentClubId = 0;

    public void setClubController(ClubController controller) {
        this.clubController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        if (clubController == null) clubController = new ClubController(null);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f7f6;");

        // --- SIDEBAR ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("ADMINISTRATION");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnClubs = createMenuButton("🏢 Gestion Clubs", true);
        // Suppression du bouton Gestion Users
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnLogout);

        // --- MAIN CONTENT AREA ---
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));

        Label titleLabel = new Label("Système de Gestion des Clubs");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // --- FORMULAIRE ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15); formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1; -fx-border-radius: 10;");

        clubNameField = new TextField(); clubDescriptionField = new TextField();
        clubTypeField = new TextField(); meetingScheduleField = new TextField();
        maxCapacityField = new TextField(); memberIdField = new TextField();
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        formGrid.add(new Label("Nom du Club :"), 0, 0); formGrid.add(clubNameField, 1, 0);
        formGrid.add(new Label("Type :"), 2, 0); formGrid.add(clubTypeField, 3, 0);
        formGrid.add(new Label("Description :"), 0, 1); formGrid.add(clubDescriptionField, 1, 1, 3, 1);
        formGrid.add(new Label("Horaire :"), 0, 2); formGrid.add(meetingScheduleField, 1, 2);
        formGrid.add(new Label("Capacité Max :"), 2, 2); formGrid.add(maxCapacityField, 3, 2);

        HBox actionButtons = new HBox(10);
        Button addBtn = new Button("➕ Ajouter");
        Button updateBtn = new Button("💾 Modifier");
        Button deleteBtn = new Button("🗑 Supprimer");
        Button clearBtn = new Button("🧹 Vider");

        styleButton(addBtn, "#2ecc71");
        styleButton(updateBtn, "#f1c40f");
        styleButton(deleteBtn, "#e74c3c");
        styleButton(clearBtn, "#95a5a6");

        actionButtons.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);
        formGrid.add(actionButtons, 1, 3, 3, 1);

        HBox memberBox = new HBox(10);
        memberBox.setAlignment(Pos.CENTER_LEFT);
        memberBox.setPadding(new Insets(10, 0, 0, 0));
        memberIdField.setPromptText("ID Utilisateur");
        Button addMemberBtn = new Button("Inscrire Membre");
        styleButton(addMemberBtn, "#3498db");
        memberBox.getChildren().addAll(new Label("Quick Enroll :"), memberIdField, addMemberBtn);
        formGrid.add(memberBox, 0, 4, 4, 1);

        // --- TABLEAU ---
        clubTable = new TableView<>();
        setupTable();

        mainContent.getChildren().addAll(titleLabel, formGrid, messageLabel, clubTable);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        // --- ACTIONS ---
        addBtn.setOnAction(e -> handleAdd());
        updateBtn.setOnAction(e -> handleUpdate());
        deleteBtn.setOnAction(e -> handleDelete());
        addMemberBtn.setOnAction(e -> handleAddMember());
        clearBtn.setOnAction(e -> clearFields());

        // LOGIQUE DE DÉCONNEXION
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("Sportify Admin - Gestion de Structure");
        primaryStage.setScene(scene);
        refreshClubList();
        primaryStage.show();
    }

    /**
     * Ferme la session actuelle et retourne à l'écran de login
     */
    private void handleLogout(Stage currentStage) {
        currentStage.close(); // Ferme l'admin panel
        LoginFrame loginFrame = new LoginFrame();
        try {
            loginFrame.start(new Stage()); // Ouvre une nouvelle fenêtre de login
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        TableColumn<Club, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Club, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Club, Integer> countCol = new TableColumn<>("Membres");
        countCol.setCellValueFactory(new PropertyValueFactory<>("currentMemberCount"));
        TableColumn<Club, Integer> capCol = new TableColumn<>("Capacité Max");
        capCol.setCellValueFactory(new PropertyValueFactory<>("maxCapacity"));

        clubTable.getColumns().addAll(nameCol, typeCol, countCol, capCol);
        clubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        clubTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            if (newSelection != null) {
                currentClubId = newSelection.getClubID();
                clubNameField.setText(newSelection.getName());
                clubDescriptionField.setText(newSelection.getDescription());
                clubTypeField.setText(newSelection.getType());
                meetingScheduleField.setText(newSelection.getMeetingSchedule());
                maxCapacityField.setText(String.valueOf(newSelection.getMaxCapacity()));
            }
        });
    }

    private void handleAdd() {
        try {
            clubController.createClub(0, clubNameField.getText(), clubDescriptionField.getText(), clubTypeField.getText(), meetingScheduleField.getText(), Integer.parseInt(maxCapacityField.getText()));
            refreshClubList(); clearFields(); messageLabel.setText("Club ajouté avec succès !");
        } catch (Exception e) { messageLabel.setText("Erreur : " + e.getMessage()); }
    }

    private void handleUpdate() {
        if (currentClubId == 0) return;
        try {
            Club c = new Club(currentClubId, clubNameField.getText(), clubDescriptionField.getText(), clubTypeField.getText(), meetingScheduleField.getText(), Integer.parseInt(maxCapacityField.getText()));
            clubController.updateClub(c); refreshClubList(); messageLabel.setText("Club mis à jour !");
        } catch (Exception e) { messageLabel.setText("Erreur : " + e.getMessage()); }
    }

    private void handleDelete() {
        if (currentClubId == 0) return;
        try {
            clubController.deleteClub(currentClubId);
            refreshClubList(); clearFields(); messageLabel.setText("Club supprimé.");
        } catch (SQLException e) { messageLabel.setText("Erreur suppression : " + e.getMessage()); }
    }

    private void handleAddMember() {
        if (currentClubId == 0) return;
        try {
            if (clubController.addMemberToClub(currentClubId, memberIdField.getText())) {
                refreshClubList(); messageLabel.setText("Membre inscrit !");
            }
        } catch (SQLException e) { messageLabel.setText("Erreur : " + e.getMessage()); }
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        String baseStyle = "-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
        return btn;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand;");
    }

    private void refreshClubList() {
        try {
            List<Club> clubs = clubController.getAllClubs();
            clubTable.setItems(FXCollections.observableArrayList(clubs));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearFields() {
        clubNameField.clear(); clubDescriptionField.clear(); clubTypeField.clear();
        meetingScheduleField.clear(); maxCapacityField.clear(); memberIdField.clear();
        currentClubId = 0;
    }

    public static void main(String[] args) { launch(args); }
}