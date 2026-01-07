package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.TypeSport;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClubManagementFrame extends Application {
    private ClubController clubController;
    private TypeSportController sportController = new TypeSportController();

    // Navigation & Layout
    private StackPane contentArea;
    private VBox clubView, sportView;
    private Button btnClubs, btnSports;

    // --- ÉLÉMENTS CLUB ---
    private TextField clubNameField, clubDescriptionField, clubTypeField, meetingScheduleField, maxCapacityField, memberIdField;
    private TableView<Club> clubTable;
    private Label clubMessageLabel;
    private int currentClubId = 0;

    // --- ÉLÉMENTS TYPE SPORT (Intégration Ami) ---
    private TextField sportNomField = new TextField();
    private TextField sportNbJoueursField = new TextField();
    private TextArea sportDescField = new TextArea();
    private TextArea sportRolesField = new TextArea();
    private TextArea sportStatsField = new TextArea();
    private TableView<TypeSport> sportTable;
    private TypeSport selectedTypeSport = null;

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

        btnClubs = createMenuButton("🏢 Gestion Clubs", true);
        btnSports = createMenuButton("⚙ Type de Sports", false);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnSports, btnLogout);

        // --- PRÉPARATION DES VUES ---
        createClubView();
        createSportView();

        contentArea = new StackPane(clubView, sportView);
        sportView.setVisible(false); // Club visible par défaut

        // --- LOGIQUE DE NAVIGATION ---
        btnClubs.setOnAction(e -> switchView(clubView, btnClubs));
        btnSports.setOnAction(e -> {
            switchView(sportView, btnSports);
            refreshSportList();
        });
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Sportify Admin - Club & Sport Management");
        primaryStage.setScene(scene);
        refreshClubList();
        primaryStage.show();
    }

    // ==========================================
    // VUE 1 : GESTION DES CLUBS (TON CODE)
    // ==========================================
    private void createClubView() {
        clubView = new VBox(20);
        clubView.setPadding(new Insets(30));

        Label title = new Label("Système de Gestion des Clubs");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15); formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        clubNameField = new TextField(); clubDescriptionField = new TextField();
        clubTypeField = new TextField(); meetingScheduleField = new TextField();
        maxCapacityField = new TextField(); memberIdField = new TextField();
        clubMessageLabel = new Label();
        clubMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        formGrid.add(new Label("Nom du Club :"), 0, 0); formGrid.add(clubNameField, 1, 0);
        formGrid.add(new Label("Type :"), 2, 0); formGrid.add(clubTypeField, 3, 0);
        formGrid.add(new Label("Description :"), 0, 1); formGrid.add(clubDescriptionField, 1, 1, 3, 1);
        formGrid.add(new Label("Horaire :"), 0, 2); formGrid.add(meetingScheduleField, 1, 2);
        formGrid.add(new Label("Capacité Max :"), 2, 2); formGrid.add(maxCapacityField, 3, 2);

        Button addBtn = new Button("➕ Ajouter"); styleButton(addBtn, "#2ecc71");
        Button updateBtn = new Button("💾 Modifier"); styleButton(updateBtn, "#f1c40f");
        Button deleteBtn = new Button("🗑 Supprimer"); styleButton(deleteBtn, "#e74c3c");
        Button clearBtn = new Button("🧹 Vider"); styleButton(clearBtn, "#95a5a6");

        HBox actions = new HBox(10, addBtn, updateBtn, deleteBtn, clearBtn);
        formGrid.add(actions, 1, 3, 3, 1);

        clubTable = new TableView<>();
        setupClubTable();

        clubView.getChildren().addAll(title, formGrid, clubMessageLabel, clubTable);

        addBtn.setOnAction(e -> handleAddClub());
        updateBtn.setOnAction(e -> handleUpdateClub());
        deleteBtn.setOnAction(e -> handleDeleteClub());
        clearBtn.setOnAction(e -> clearClubFields());
    }

    // ==========================================
    // VUE 2 : GESTION DES SPORTS (CODE AMI INTÉGRÉ)
    // ==========================================
    private void createSportView() {
        sportView = new VBox(20);
        sportView.setPadding(new Insets(30));

        Label title = new Label("Configuration des Disciplines Sportives");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        sportDescField.setPrefRowCount(2);
        sportRolesField.setPrefRowCount(3);
        sportStatsField.setPrefRowCount(3);

        grid.add(new Label("Nom du Sport :"), 0, 0); grid.add(sportNomField, 1, 0);
        grid.add(new Label("Nb Joueurs :"), 2, 0); grid.add(sportNbJoueursField, 3, 0);
        grid.add(new Label("Description :"), 0, 1); grid.add(sportDescField, 1, 1, 3, 1);
        grid.add(new Label("Rôles (1/ligne) :"), 0, 2); grid.add(sportRolesField, 1, 2);
        grid.add(new Label("Stats (1/ligne) :"), 2, 2); grid.add(sportStatsField, 3, 2);

        Button addSportBtn = new Button("➕ Créer"); styleButton(addSportBtn, "#3498db");
        Button updateSportBtn = new Button("💾 Modifier"); styleButton(updateSportBtn, "#f39c12");
        Button deleteSportBtn = new Button("🗑 Supprimer"); styleButton(deleteSportBtn, "#e74c3c");
        Button clearSportBtn = new Button("🧹 Vider"); styleButton(clearSportBtn, "#95a5a6");

        HBox sportActions = new HBox(10, addSportBtn, updateSportBtn, deleteSportBtn, clearSportBtn);
        grid.add(sportActions, 1, 3, 3, 1);

        sportTable = new TableView<>();
        setupSportTable();

        sportView.getChildren().addAll(title, grid, sportTable);

        addSportBtn.setOnAction(e -> handleAddSport());
        updateSportBtn.setOnAction(e -> handleUpdateSport());
        deleteSportBtn.setOnAction(e -> handleDeleteSport());
        clearSportBtn.setOnAction(e -> clearSportFields());
    }

    // --- LOGIQUE MÉTIER SPORT ---
    private void handleAddSport() {
        try {
            sportController.handleCreateTypeSport(sportNomField.getText(), sportDescField.getText(),
                    Integer.parseInt(sportNbJoueursField.getText()),
                    parseTextArea(sportRolesField.getText()), parseTextArea(sportStatsField.getText()));
            refreshSportList(); clearSportFields();
        } catch (Exception e) { showError("Erreur", "Saisie invalide ou nombre requis."); }
    }

    private void handleUpdateSport() {
        if (selectedTypeSport == null) return;
        try {
            selectedTypeSport.setNom(sportNomField.getText());
            selectedTypeSport.setDescription(sportDescField.getText());
            selectedTypeSport.setNbJoueurs(Integer.parseInt(sportNbJoueursField.getText()));
            selectedTypeSport.setRoles(parseTextArea(sportRolesField.getText()));
            selectedTypeSport.setStatistiques(parseTextArea(sportStatsField.getText()));
            sportController.handleUpdateTypeSport(selectedTypeSport);
            refreshSportList(); clearSportFields();
        } catch (Exception e) { showError("Erreur", "Mise à jour impossible."); }
    }

    private void handleDeleteSport() {
        if (selectedTypeSport == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer le sport " + selectedTypeSport.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                sportController.handleDeleteTypeSport(selectedTypeSport.getId());
                refreshSportList(); clearSportFields();
            }
        });
    }

    // ==========================================
    // UTILS & NAVIGATION
    // ==========================================
    private void switchView(VBox view, Button activeBtn) {
        clubView.setVisible(false);
        sportView.setVisible(false);
        view.setVisible(true);

        btnClubs.setStyle(createMenuButtonStyle(btnClubs == activeBtn));
        btnSports.setStyle(createMenuButtonStyle(btnSports == activeBtn));
    }

    private String createMenuButtonStyle(boolean active) {
        return "-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10;";
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand; -fx-background-radius: 5;");
    }

    private List<String> parseTextArea(String text) {
        if (text == null || text.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(text.split("\n")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private void refreshSportList() {
        sportTable.setItems(FXCollections.observableArrayList(sportController.handleGetAllTypeSports()));
    }

    private void clearSportFields() {
        sportNomField.clear(); sportDescField.clear(); sportNbJoueursField.clear();
        sportRolesField.clear(); sportStatsField.clear(); selectedTypeSport = null;
    }

    private void setupSportTable() {
        TableColumn<TypeSport, String> nomCol = new TableColumn<>("Discipline");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<TypeSport, Integer> nbCol = new TableColumn<>("Nb Joueurs");
        nbCol.setCellValueFactory(new PropertyValueFactory<>("nbJoueurs"));

        sportTable.getColumns().addAll(nomCol, nbCol);
        sportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        sportTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                selectedTypeSport = newVal;
                sportNomField.setText(newVal.getNom());
                sportDescField.setText(newVal.getDescription());
                sportNbJoueursField.setText(String.valueOf(newVal.getNbJoueurs()));
                sportRolesField.setText(newVal.getRoles() != null ? String.join("\n", newVal.getRoles()) : "");
                sportStatsField.setText(newVal.getStatistiques() != null ? String.join("\n", newVal.getStatistiques()) : "");
            }
        });
    }

    // --- LOGIQUE CLUB ---
    private void setupClubTable() {
        TableColumn<Club, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Club, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        clubTable.getColumns().addAll(nameCol, typeCol);
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

    private void handleAddClub() {
        try {
            clubController.createClub(0, clubNameField.getText(), clubDescriptionField.getText(), clubTypeField.getText(), meetingScheduleField.getText(), Integer.parseInt(maxCapacityField.getText()));
            refreshClubList(); clearClubFields(); clubMessageLabel.setText("Club ajouté !");
        } catch (Exception e) { clubMessageLabel.setText("Erreur : " + e.getMessage()); }
    }

    private void handleUpdateClub() {
        if (currentClubId == 0) return;
        try {
            Club c = new Club(currentClubId, clubNameField.getText(), clubDescriptionField.getText(), clubTypeField.getText(), meetingScheduleField.getText(), Integer.parseInt(maxCapacityField.getText()));
            clubController.updateClub(c); refreshClubList(); clubMessageLabel.setText("Club mis à jour !");
        } catch (Exception e) { clubMessageLabel.setText("Erreur mise à jour."); }
    }

    private void handleDeleteClub() {
        if (currentClubId == 0) return;
        try {
            clubController.deleteClub(currentClubId);
            refreshClubList(); clearClubFields(); clubMessageLabel.setText("Club supprimé.");
        } catch (SQLException e) { clubMessageLabel.setText("Erreur suppression."); }
    }

    private void refreshClubList() {
        try { clubTable.setItems(FXCollections.observableArrayList(clubController.getAllClubs())); } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearClubFields() {
        clubNameField.clear(); clubDescriptionField.clear(); clubTypeField.clear();
        meetingScheduleField.clear(); maxCapacityField.clear(); currentClubId = 0;
    }

    private void handleLogout(Stage currentStage) {
        currentStage.close();
        new LoginFrame().start(new Stage());
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(createMenuButtonStyle(active));
        return btn;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title); alert.setContentText(content); alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}