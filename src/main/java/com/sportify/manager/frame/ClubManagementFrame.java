package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.controllers.LicenceController;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.StatutLicence;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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
    private LicenceController licenceController = new LicenceController();

    // Navigation & Layout
    private StackPane contentArea;
    private VBox clubView, sportView, licenceAdminView;
    private Button btnClubs, btnSports, btnLicences;

    // --- ÉLÉMENTS CLUB ---
    private TextField clubNameField, clubDescriptionField, clubTypeField, meetingScheduleField, maxCapacityField;
    private TableView<Club> clubTable;
    private Label clubMessageLabel;
    private int currentClubId = 0;

    // --- ÉLÉMENTS TYPE SPORT ---
    private TextField sportNomField = new TextField();
    private TextField sportNbJoueursField = new TextField();
    private TextArea sportDescField = new TextArea();
    private TextArea sportRolesField = new TextArea();
    private TextArea sportStatsField = new TextArea();
    private TableView<TypeSport> sportTable;
    private TypeSport selectedTypeSport = null;

    // --- ÉLÉMENTS LICENCES (CRITÈRE 7.2 & 7.5) ---
    private TableView<Licence> pendingLicenceTable;
    private TextArea adminCommentField;

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
        btnLicences = createMenuButton("📜 Valider Licences", false);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnSports, btnLicences, btnLogout);

        // --- PRÉPARATION DES VUES ---
        createClubView();
        createSportView();
        createLicenceAdminView();

        contentArea = new StackPane(clubView, sportView, licenceAdminView);
        sportView.setVisible(false);
        licenceAdminView.setVisible(false);

        // --- LOGIQUE DE NAVIGATION ---
        btnClubs.setOnAction(e -> switchView(clubView, btnClubs));
        btnSports.setOnAction(e -> {
            switchView(sportView, btnSports);
            refreshSportList();
        });
        btnLicences.setOnAction(e -> {
            switchView(licenceAdminView, btnLicences);
            refreshPendingLicences();
        });
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Sportify Admin - Système de Gestion Global");
        primaryStage.setScene(scene);
        refreshClubList();
        primaryStage.show();
    }

    // ==========================================
    // VUE 3 : VALIDATION DES LICENCES (NOUVEAU)
    // ==========================================
    private void createLicenceAdminView() {
        licenceAdminView = new VBox(20);
        licenceAdminView.setPadding(new Insets(30));

        Label title = new Label("Gestion des Licences (Validation)");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        pendingLicenceTable = new TableView<>();
        setupLicenceTable();

        VBox decisionBox = new VBox(10);
        decisionBox.setPadding(new Insets(20));
        decisionBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        adminCommentField = new TextArea();
        adminCommentField.setPromptText("Motif du refus ou commentaire de validation...");
        adminCommentField.setPrefRowCount(3);

        Button btnApprove = new Button("✅ Valider la Licence");
        styleButton(btnApprove, "#2ecc71");

        Button btnReject = new Button("❌ Refuser la Demande");
        styleButton(btnReject, "#e74c3c");

        HBox actionButtons = new HBox(15, btnApprove, btnReject);
        decisionBox.getChildren().addAll(new Label("Commentaire Administratif :"), adminCommentField, actionButtons);

        licenceAdminView.getChildren().addAll(title, new Label("Demandes en attente (Critère 7.2) :"), pendingLicenceTable, decisionBox);

        btnApprove.setOnAction(e -> handleLicenceDecision(true));
        btnReject.setOnAction(e -> handleLicenceDecision(false));
    }

    private void setupLicenceTable() {
        TableColumn<Licence, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Licence, String> memberCol = new TableColumn<>("Membre");
        memberCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMembre().getName()));

        TableColumn<Licence, String> sportCol = new TableColumn<>("Discipline");
        sportCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSport().getNom()));

        TableColumn<Licence, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeLicence"));

        pendingLicenceTable.getColumns().addAll(idCol, memberCol, sportCol, typeCol);
        pendingLicenceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void handleLicenceDecision(boolean approved) {
        Licence selected = pendingLicenceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection requise", "Veuillez sélectionner une demande dans la liste.");
            return;
        }

        licenceController.validerLicence(selected.getId(), approved, adminCommentField.getText());
        adminCommentField.clear();
        refreshPendingLicences();

        String msg = approved ? "Licence validée avec succès. Elle est désormais ACTIVE." : "Demande de licence refusée.";
        showAlert(Alert.AlertType.INFORMATION, "Décision enregistrée", msg);
    }

    private void refreshPendingLicences() {
        List<Licence> pending = licenceController.getLicencesByStatut(StatutLicence.EN_ATTENTE);
        pendingLicenceTable.setItems(FXCollections.observableArrayList(pending));
    }

    // ==========================================
    // VUE 1 : GESTION DES CLUBS
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
        maxCapacityField = new TextField();
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
    // VUE 2 : GESTION DES SPORTS
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

        HBox sportActions = new HBox(10, addSportBtn, updateSportBtn, deleteSportBtn);
        grid.add(sportActions, 1, 3, 3, 1);

        sportTable = new TableView<>();
        setupSportTable();

        sportView.getChildren().addAll(title, grid, sportTable);

        addSportBtn.setOnAction(e -> handleAddSport());
        updateSportBtn.setOnAction(e -> handleUpdateSport());
        deleteSportBtn.setOnAction(e -> handleDeleteSport());
    }

    // --- LOGIQUE COMMUNE & UTILS ---
    private void switchView(VBox view, Button activeBtn) {
        clubView.setVisible(false);
        sportView.setVisible(false);
        licenceAdminView.setVisible(false);
        view.setVisible(true);

        btnClubs.setStyle(createMenuButtonStyle(btnClubs == activeBtn));
        btnSports.setStyle(createMenuButtonStyle(btnSports == activeBtn));
        btnLicences.setStyle(createMenuButtonStyle(btnLicences == activeBtn));
    }

    private String createMenuButtonStyle(boolean active) {
        return "-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10;";
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand; -fx-background-radius: 5;");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait();
    }

    private void showError(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
    }

    private void handleLogout(Stage currentStage) {
        currentStage.close();
        new LoginFrame().start(new Stage());
    }

    // --- LOGIQUE CLUB & SPORT (Même que précédemment) ---
    private void refreshClubList() {
        try { clubTable.setItems(FXCollections.observableArrayList(clubController.getAllClubs())); } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshSportList() {
        sportTable.setItems(FXCollections.observableArrayList(sportController.handleGetAllTypeSports()));
    }

    private void setupClubTable() {
        TableColumn<Club, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Club, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        clubTable.getColumns().addAll(nameCol, typeCol);
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

    private void setupSportTable() {
        TableColumn<TypeSport, String> nomCol = new TableColumn<>("Discipline");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        sportTable.getColumns().add(nomCol);
        sportTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                selectedTypeSport = newVal;
                sportNomField.setText(newVal.getNom());
                sportDescField.setText(newVal.getDescription());
                sportNbJoueursField.setText(String.valueOf(newVal.getNbJoueurs()));
            }
        });
    }

    private void handleAddClub() {
        try {
            clubController.createClub(0, clubNameField.getText(), clubDescriptionField.getText(), clubTypeField.getText(), meetingScheduleField.getText(), Integer.parseInt(maxCapacityField.getText()));
            refreshClubList(); clearClubFields();
        } catch (Exception e) { clubMessageLabel.setText("Erreur : " + e.getMessage()); }
    }

    private void handleUpdateClub() {
        if (currentClubId == 0) return;
        try {
            Club c = new Club(currentClubId, clubNameField.getText(), clubDescriptionField.getText(), clubTypeField.getText(), meetingScheduleField.getText(), Integer.parseInt(maxCapacityField.getText()));
            clubController.updateClub(c); refreshClubList();
        } catch (Exception e) { clubMessageLabel.setText("Erreur mise à jour."); }
    }

    private void handleDeleteClub() {
        if (currentClubId == 0) return;
        try {
            clubController.deleteClub(currentClubId);
            refreshClubList(); clearClubFields();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearClubFields() {
        clubNameField.clear(); clubDescriptionField.clear(); clubTypeField.clear();
        meetingScheduleField.clear(); maxCapacityField.clear(); currentClubId = 0;
    }

    private void handleAddSport() {
        try {
            sportController.handleCreateTypeSport(sportNomField.getText(), sportDescField.getText(),
                    Integer.parseInt(sportNbJoueursField.getText()), new ArrayList<>(), new ArrayList<>());
            refreshSportList();
        } catch (Exception e) { showError("Erreur", "Saisie invalide."); }
    }

    private void handleUpdateSport() {
        if (selectedTypeSport == null) return;
        selectedTypeSport.setNom(sportNomField.getText());
        selectedTypeSport.setDescription(sportDescField.getText());
        sportController.handleUpdateTypeSport(selectedTypeSport);
        refreshSportList();
    }

    private void handleDeleteSport() {
        if (selectedTypeSport == null) return;
        sportController.handleDeleteTypeSport(selectedTypeSport.getId());
        refreshSportList();
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(createMenuButtonStyle(active));
        return btn;
    }

    public static void main(String[] args) { launch(args); }
}