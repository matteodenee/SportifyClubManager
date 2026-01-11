package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.EquipmentTypeController;
import com.sportify.manager.controllers.LicenceController;
import com.sportify.manager.controllers.MatchController;
import com.sportify.manager.controllers.MatchRequestController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.EquipmentType;
import com.sportify.manager.services.EquipmentTypeActionResult;
import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchStatus;
import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.User;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFrame extends Application {
    private ClubController clubController;
    private TypeSportController sportController = new TypeSportController();
    private EquipmentTypeController equipmentTypeController = new EquipmentTypeController();
    private LicenceController licenceController = new LicenceController();
    private MatchController matchController = MatchController.getInstance();
    private MatchRequestController matchRequestController = MatchRequestController.getInstance();
    private User currentUser;

    // Navigation & Layout
    private StackPane contentArea;
    private VBox clubView, sportView, equipmentTypeView, licenceAdminView, matchView;
    private Button btnClubs, btnSports, btnEquipmentTypes, btnLicences, btnMatchs;

    // --- ÉLÉMENTS CLUB ---
    private TextField clubNameField, clubDescriptionField, meetingScheduleField, maxCapacityField;
    private ComboBox<TypeSport> clubSportCombo;
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

    // --- ELEMENTS TYPE EQUIPEMENT ---
    private TextField equipmentTypeNameField;
    private TextArea equipmentTypeDescriptionField;
    private TableView<EquipmentType> equipmentTypeTable;
    private EquipmentType selectedEquipmentType = null;
    private Label equipmentTypeMessageLabel;
    private Button equipmentTypeUpdateBtn;
    private Button equipmentTypeDeleteBtn;
    // --- ÉLÉMENTS LICENCES (CRITÈRE 7.2 & 7.5) ---
    private TableView<Licence> pendingLicenceTable;
    private TextArea adminCommentField;

    // --- ÉLÉMENTS MATCHS ---
    private TableView<Match> matchTable;
    private TableView<MatchRequest> matchRequestTable;
    private ComboBox<TypeSport> matchSportCombo;
    private ComboBox<Club> matchHomeClubCombo;
    private ComboBox<Club> matchAwayClubCombo;
    private DatePicker matchDatePicker;
    private TextField matchTimeField;
    private DatePicker matchDeadlineDatePicker;
    private TextField matchDeadlineTimeField;
    private TextField matchLocationField;
    private TextField matchRefereeField;
    private ComboBox<MatchStatus> matchStatusCombo;
    private TextField matchHomeScoreField;
    private TextField matchAwayScoreField;
    private Label matchMessageLabel;
    private int currentMatchId = 0;
    private List<Club> matchClubCache = new ArrayList<>();
    private List<TypeSport> matchSportCache = new ArrayList<>();
    private List<MatchRequest> matchRequestCache = new ArrayList<>();

    public void setClubController(ClubController controller) {
        this.clubController = controller;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
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
        btnEquipmentTypes = createMenuButton("🏷️ Types d'equipement", false);
        btnLicences = createMenuButton("📜 Valider Licences", false);
        btnMatchs = createMenuButton("⚽ Gestion Matchs", false);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(
                menuLabel,
                new Separator(),
                btnClubs,
                btnSports,
                btnEquipmentTypes,
                btnLicences,
                btnMatchs,
                btnLogout
        );

        // --- PRÉPARATION DES VUES ---
        createClubView();
        createSportView();
        createEquipmentTypeView();
        createLicenceAdminView();
        createMatchView();

        contentArea = new StackPane(clubView, sportView, equipmentTypeView, licenceAdminView, matchView);
        sportView.setVisible(false);
        equipmentTypeView.setVisible(false);
        licenceAdminView.setVisible(false);
        matchView.setVisible(false);

        // --- LOGIQUE DE NAVIGATION ---
        btnClubs.setOnAction(e -> {
            switchView(clubView, btnClubs);
            refreshSportChoices();
        });
        btnSports.setOnAction(e -> {
            switchView(sportView, btnSports);
            refreshSportList();
        });
        btnEquipmentTypes.setOnAction(e -> {
            if (!isAdminUser()) {
                showError("Acces refuse", "Seul un administrateur peut acceder a ce module.");
                return;
            }
            switchView(equipmentTypeView, btnEquipmentTypes);
            refreshEquipmentTypeList();
        });
        btnLicences.setOnAction(e -> {
            switchView(licenceAdminView, btnLicences);
            refreshPendingLicences();
        });
        btnMatchs.setOnAction(e -> {
            switchView(matchView, btnMatchs);
            refreshMatchChoices();
            refreshMatchList();
            refreshMatchRequestList();
        });
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Sportify Admin - Système de Gestion Global");
        primaryStage.setScene(scene);
        refreshSportChoices();
        refreshClubList();
        refreshMatchChoices();
        refreshMatchList();
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
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        clubNameField = new TextField();
        clubDescriptionField = new TextField();
        clubSportCombo = new ComboBox<>();
        clubSportCombo.setPromptText("Choisir un sport");
        meetingScheduleField = new TextField();
        maxCapacityField = new TextField();
        clubMessageLabel = new Label();
        clubMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        formGrid.add(new Label("Nom du Club :"), 0, 0);
        formGrid.add(clubNameField, 1, 0);
        formGrid.add(new Label("Sport :"), 2, 0);
        formGrid.add(clubSportCombo, 3, 0);
        formGrid.add(new Label("Description :"), 0, 1);
        formGrid.add(clubDescriptionField, 1, 1, 3, 1);
        formGrid.add(new Label("Horaire :"), 0, 2);
        formGrid.add(meetingScheduleField, 1, 2);
        formGrid.add(new Label("Capacité Max :"), 2, 2);
        formGrid.add(maxCapacityField, 3, 2);

        Button addBtn = new Button("➕ Ajouter");
        styleButton(addBtn, "#2ecc71");
        Button updateBtn = new Button("💾 Modifier");
        styleButton(updateBtn, "#f1c40f");
        Button deleteBtn = new Button("🗑 Supprimer");
        styleButton(deleteBtn, "#e74c3c");
        Button clearBtn = new Button("🧹 Vider");
        styleButton(clearBtn, "#95a5a6");

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
    // VUE 4 : GESTION DES MATCHS
    // ==========================================
    private void createMatchView() {
        matchView = new VBox(20);
        matchView.setPadding(new Insets(30));

        Label title = new Label("Gestion des Matchs");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        matchSportCombo = new ComboBox<>();
        matchSportCombo.setPromptText("Sport");
        matchHomeClubCombo = new ComboBox<>();
        matchHomeClubCombo.setPromptText("Équipe domicile");
        matchAwayClubCombo = new ComboBox<>();
        matchAwayClubCombo.setPromptText("Équipe extérieure");
        matchDatePicker = new DatePicker();
        matchTimeField = new TextField();
        matchTimeField.setPromptText("HH:mm");
        matchDeadlineDatePicker = new DatePicker();
        matchDeadlineTimeField = new TextField();
        matchDeadlineTimeField.setPromptText("HH:mm");
        matchLocationField = new TextField();
        matchRefereeField = new TextField();
        matchStatusCombo = new ComboBox<>(FXCollections.observableArrayList(MatchStatus.values()));
        matchStatusCombo.setPromptText("Statut");
        matchHomeScoreField = new TextField();
        matchAwayScoreField = new TextField();
        matchMessageLabel = new Label();
        matchMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        formGrid.add(new Label("Sport :"), 0, 0);
        formGrid.add(matchSportCombo, 1, 0);
        formGrid.add(new Label("Date :"), 2, 0);
        formGrid.add(matchDatePicker, 3, 0);

        formGrid.add(new Label("Domicile :"), 0, 1);
        formGrid.add(matchHomeClubCombo, 1, 1);
        formGrid.add(new Label("Heure :"), 2, 1);
        formGrid.add(matchTimeField, 3, 1);

        formGrid.add(new Label("Extérieur :"), 0, 2);
        formGrid.add(matchAwayClubCombo, 1, 2);
        formGrid.add(new Label("Lieu :"), 2, 2);
        formGrid.add(matchLocationField, 3, 2);

        formGrid.add(new Label("Arbitre :"), 0, 3);
        formGrid.add(matchRefereeField, 1, 3);
        formGrid.add(new Label("Statut :"), 2, 3);
        formGrid.add(matchStatusCombo, 3, 3);

        formGrid.add(new Label("Deadline compo :"), 0, 4);
        formGrid.add(matchDeadlineDatePicker, 1, 4);
        formGrid.add(new Label("Heure :"), 2, 4);
        formGrid.add(matchDeadlineTimeField, 3, 4);

        formGrid.add(new Label("Score Domicile :"), 0, 5);
        formGrid.add(matchHomeScoreField, 1, 5);
        formGrid.add(new Label("Score Extérieur :"), 2, 5);
        formGrid.add(matchAwayScoreField, 3, 5);

        Button addBtn = new Button("➕ Planifier");
        styleButton(addBtn, "#2ecc71");
        Button updateBtn = new Button("💾 Modifier");
        styleButton(updateBtn, "#f1c40f");
        Button clearBtn = new Button("🧹 Vider");
        styleButton(clearBtn, "#95a5a6");

        HBox actions = new HBox(10, addBtn, updateBtn, clearBtn);
        formGrid.add(actions, 1, 6, 3, 1);

        matchTable = new TableView<>();
        setupMatchTable();

        matchRequestTable = new TableView<>();
        setupMatchRequestTable();

        HBox requestActions = new HBox(10);
        Button approveBtn = new Button("✅ Valider");
        styleButton(approveBtn, "#2ecc71");
        Button rejectBtn = new Button("❌ Refuser");
        styleButton(rejectBtn, "#e74c3c");
        requestActions.getChildren().addAll(approveBtn, rejectBtn);
        approveBtn.setOnAction(e -> handleApproveMatchRequest());
        rejectBtn.setOnAction(e -> handleRejectMatchRequest());

        matchView.getChildren().addAll(
                title,
                formGrid,
                matchMessageLabel,
                new Label("Demandes de match (Coach) :"),
                matchRequestTable,
                requestActions,
                new Label("Matchs planifiés :"),
                matchTable
        );

        addBtn.setOnAction(e -> handleAddMatch());
        updateBtn.setOnAction(e -> handleUpdateMatch());
        clearBtn.setOnAction(e -> clearMatchFields());
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
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");
        grid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints colLabel = new ColumnConstraints();
        colLabel.setMinWidth(120);
        ColumnConstraints colInput = new ColumnConstraints();
        colInput.setHgrow(Priority.ALWAYS);
        ColumnConstraints colLabel2 = new ColumnConstraints();
        colLabel2.setMinWidth(120);
        ColumnConstraints colInput2 = new ColumnConstraints();
        colInput2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(colLabel, colInput, colLabel2, colInput2);

        sportDescField.setPrefRowCount(2);
        sportDescField.setWrapText(true);
        sportRolesField.setPrefRowCount(3);
        sportRolesField.setWrapText(true);
        sportStatsField.setPrefRowCount(3);
        sportStatsField.setWrapText(true);

        sportNomField.setMaxWidth(Double.MAX_VALUE);
        sportNbJoueursField.setMaxWidth(Double.MAX_VALUE);
        sportDescField.setMaxWidth(Double.MAX_VALUE);
        sportRolesField.setMaxWidth(Double.MAX_VALUE);
        sportStatsField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(sportNomField, Priority.ALWAYS);
        GridPane.setHgrow(sportNbJoueursField, Priority.ALWAYS);
        GridPane.setHgrow(sportDescField, Priority.ALWAYS);
        GridPane.setHgrow(sportRolesField, Priority.ALWAYS);
        GridPane.setHgrow(sportStatsField, Priority.ALWAYS);

        grid.add(new Label("Nom du Sport :"), 0, 0);
        grid.add(sportNomField, 1, 0);
        grid.add(new Label("Nb Joueurs :"), 2, 0);
        grid.add(sportNbJoueursField, 3, 0);
        grid.add(new Label("Description :"), 0, 1);
        grid.add(sportDescField, 1, 1, 3, 1);
        grid.add(new Label("Rôles (1/ligne) :"), 0, 2);
        grid.add(sportRolesField, 1, 2);
        grid.add(new Label("Stats (1/ligne) :"), 2, 2);
        grid.add(sportStatsField, 3, 2);

        Button addSportBtn = new Button("➕ Créer");
        styleButton(addSportBtn, "#3498db");
        Button updateSportBtn = new Button("💾 Modifier");
        styleButton(updateSportBtn, "#f39c12");
        Button deleteSportBtn = new Button("🗑 Supprimer");
        styleButton(deleteSportBtn, "#e74c3c");

        HBox sportActions = new HBox(10, addSportBtn, updateSportBtn, deleteSportBtn);
        grid.add(sportActions, 1, 3, 3, 1);

        sportTable = new TableView<>();
        setupSportTable();

        sportView.getChildren().addAll(title, grid, sportTable);

        addSportBtn.setOnAction(e -> handleAddSport());
        updateSportBtn.setOnAction(e -> handleUpdateSport());
        deleteSportBtn.setOnAction(e -> handleDeleteSport());
    }

    // ==========================================
    // VUE 5 : TYPES D'EQUIPEMENT
    // ==========================================
    private void createEquipmentTypeView() {
        equipmentTypeView = new VBox(20);
        equipmentTypeView.setPadding(new Insets(30));

        Label title = new Label("Gestion des Types d'Equipement");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");
        grid.setMaxWidth(Double.MAX_VALUE);

        equipmentTypeNameField = new TextField();
        equipmentTypeDescriptionField = new TextArea();
        equipmentTypeDescriptionField.setPrefRowCount(2);
        equipmentTypeDescriptionField.setWrapText(true);

        equipmentTypeNameField.setMaxWidth(Double.MAX_VALUE);
        equipmentTypeDescriptionField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(equipmentTypeNameField, Priority.ALWAYS);
        GridPane.setHgrow(equipmentTypeDescriptionField, Priority.ALWAYS);

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(equipmentTypeNameField, 1, 0);
        grid.add(new Label("Description :"), 0, 1);
        grid.add(equipmentTypeDescriptionField, 1, 1, 3, 1);

        Button createBtn = new Button("➕ Creer");
        styleButton(createBtn, "#3498db");
        equipmentTypeUpdateBtn = new Button("💾 Modifier");
        styleButton(equipmentTypeUpdateBtn, "#f39c12");
        equipmentTypeDeleteBtn = new Button("🗑 Supprimer");
        styleButton(equipmentTypeDeleteBtn, "#e74c3c");
        equipmentTypeUpdateBtn.setDisable(true);
        equipmentTypeDeleteBtn.setDisable(true);

        HBox actions = new HBox(10, createBtn, equipmentTypeUpdateBtn, equipmentTypeDeleteBtn);
        grid.add(actions, 1, 2, 3, 1);

        equipmentTypeMessageLabel = new Label();
        equipmentTypeMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        equipmentTypeTable = new TableView<>();
        setupEquipmentTypeTable();

        equipmentTypeView.getChildren().addAll(title, grid, equipmentTypeMessageLabel, equipmentTypeTable);

        createBtn.setOnAction(e -> handleCreateEquipmentType());
        equipmentTypeUpdateBtn.setOnAction(e -> handleUpdateEquipmentType());
        equipmentTypeDeleteBtn.setOnAction(e -> handleDeleteEquipmentType());
    }

    private void setupEquipmentTypeTable() {
        TableColumn<EquipmentType, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<EquipmentType, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<EquipmentType, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        equipmentTypeTable.getColumns().addAll(idCol, nameCol, descCol);
        equipmentTypeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        equipmentTypeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            selectedEquipmentType = selected;
            boolean hasSelection = selected != null;
            equipmentTypeUpdateBtn.setDisable(!hasSelection);
            equipmentTypeDeleteBtn.setDisable(!hasSelection);
            if (hasSelection) {
                equipmentTypeNameField.setText(selected.getName());
                equipmentTypeDescriptionField.setText(selected.getDescription());
            }
        });
    }

    // --- LOGIQUE COMMUNE & UTILS ---
    private void switchView(VBox view, Button activeBtn) {
        clubView.setVisible(false);
        sportView.setVisible(false);
        equipmentTypeView.setVisible(false);
        licenceAdminView.setVisible(false);
        matchView.setVisible(false);
        view.setVisible(true);

        btnClubs.setStyle(createMenuButtonStyle(btnClubs == activeBtn));
        btnSports.setStyle(createMenuButtonStyle(btnSports == activeBtn));
        btnEquipmentTypes.setStyle(createMenuButtonStyle(btnEquipmentTypes == activeBtn));
        btnLicences.setStyle(createMenuButtonStyle(btnLicences == activeBtn));
        btnMatchs.setStyle(createMenuButtonStyle(btnMatchs == activeBtn));
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

    private void showEquipmentTypeMessage(EquipmentTypeActionResult result) {
        if (equipmentTypeMessageLabel == null || result == null) {
            return;
        }
        if (result.isSuccess()) {
            equipmentTypeMessageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else {
            equipmentTypeMessageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
        equipmentTypeMessageLabel.setText(result.getMessage());
    }

    private boolean isAdminUser() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    private void handleLogout(Stage currentStage) {
        currentStage.close();
        new LoginFrame().start(new Stage());
    }

    private void refreshClubList() {
        try {
            clubTable.setItems(FXCollections.observableArrayList(clubController.getAllClubs()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshSportList() {
        sportTable.setItems(FXCollections.observableArrayList(sportController.handleGetAllTypeSports()));
        refreshSportChoices();
    }

    private void refreshEquipmentTypeList() {
        List<EquipmentType> types = equipmentTypeController.handleListAll();
        equipmentTypeTable.setItems(FXCollections.observableArrayList(types));
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
                selectClubSport(newSelection.getSportId());
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

    private void setupMatchTable() {
        TableColumn<Match, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Match, String> sportCol = new TableColumn<>("Sport");
        sportCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getSportName(cellData.getValue().getTypeSportId())));

        TableColumn<Match, String> homeCol = new TableColumn<>("Domicile");
        homeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getClubName(cellData.getValue().getHomeTeamId())));

        TableColumn<Match, String> awayCol = new TableColumn<>("Extérieur");
        awayCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getClubName(cellData.getValue().getAwayTeamId())));

        TableColumn<Match, String> dateCol = new TableColumn<>("Date/Heure");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(formatDateTime(cellData.getValue().getDateTime())));

        TableColumn<Match, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().name()));

        TableColumn<Match, String> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(formatScore(cellData.getValue())));

        matchTable.getColumns().addAll(idCol, sportCol, homeCol, awayCol, dateCol, statusCol, scoreCol);
        matchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        matchTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                currentMatchId = selected.getId();
                selectMatchSport(selected.getTypeSportId());
                selectMatchClub(matchHomeClubCombo, selected.getHomeTeamId());
                selectMatchClub(matchAwayClubCombo, selected.getAwayTeamId());
                matchDatePicker.setValue(selected.getDateTime().toLocalDate());
                matchTimeField.setText(formatTime(selected.getDateTime().toLocalTime()));
                matchLocationField.setText(selected.getLocation());
                matchRefereeField.setText(selected.getReferee());
                matchStatusCombo.getSelectionModel().select(selected.getStatus());
                matchHomeScoreField.setText(selected.getHomeScore() == null ? "" : selected.getHomeScore().toString());
                matchAwayScoreField.setText(selected.getAwayScore() == null ? "" : selected.getAwayScore().toString());
                if (selected.getCompositionDeadline() != null) {
                    matchDeadlineDatePicker.setValue(selected.getCompositionDeadline().toLocalDate());
                    matchDeadlineTimeField.setText(formatTime(selected.getCompositionDeadline().toLocalTime()));
                } else {
                    matchDeadlineDatePicker.setValue(null);
                    matchDeadlineTimeField.clear();
                }
            }
        });
    }

    private void setupMatchRequestTable() {
        TableColumn<MatchRequest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<MatchRequest, String> requesterCol = new TableColumn<>("Club");
        requesterCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getClubName(cellData.getValue().getRequesterClubId())));

        TableColumn<MatchRequest, String> opponentCol = new TableColumn<>("Adversaire");
        opponentCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getClubName(cellData.getValue().getOpponentClubId())));

        TableColumn<MatchRequest, String> dateCol = new TableColumn<>("Date/Heure");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(formatDateTime(cellData.getValue().getRequestedDateTime())));

        TableColumn<MatchRequest, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().name()));

        matchRequestTable.getColumns().addAll(idCol, requesterCol, opponentCol, dateCol, statusCol);
        matchRequestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void handleAddClub() {
        try {
            TypeSport selectedSport = clubSportCombo.getValue();
            if (selectedSport == null) {
                clubMessageLabel.setText("Veuillez sélectionner un sport.");
                return;
            }

            clubController.createClub(
                    0, // ID du club (probablement généré automatiquement ou défini à 0)
                    clubNameField.getText(), // Nom du club
                    clubDescriptionField.getText(), // Description du club
                    selectedSport.getId(), // ID du sport
                    selectedSport.getNom(), // Nom du sport pour affichage
                    meetingScheduleField.getText(), // Horaire des réunions
                    Integer.parseInt(maxCapacityField.getText()) // Capacité du club
            );

            refreshClubList();
            clearClubFields();
            clubMessageLabel.setText("");

        } catch (Exception e) {
            clubMessageLabel.setText("Erreur : " + e.getMessage());
        }
    }





    private void handleUpdateClub() {
        if (currentClubId == 0) return;

        try {
            TypeSport selectedSport = clubSportCombo.getValue();
            if (selectedSport == null) {
                clubMessageLabel.setText("Veuillez sélectionner un sport.");
                return;
            }

            Club c = new Club(
                    currentClubId, // ID du club
                    clubNameField.getText(), // Nom du club
                    clubDescriptionField.getText(), // Description du club
                    selectedSport.getId(), // ID du sport
                    selectedSport.getNom(), // Nom du sport pour affichage
                    meetingScheduleField.getText(), // Horaire des réunions
                    Integer.parseInt(maxCapacityField.getText()) // Capacité du club
            );

            clubController.updateClub(c);

            refreshClubList();
            clubMessageLabel.setText("");
        } catch (Exception e) {
            clubMessageLabel.setText("Erreur mise à jour.");
        }
    }


    private void handleDeleteClub() {
        if (currentClubId == 0) return;
        try {
            clubController.deleteClub(currentClubId);
            refreshClubList();
            clearClubFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearClubFields() {
        clubNameField.clear();
        clubDescriptionField.clear();
        clubSportCombo.getSelectionModel().clearSelection();
        meetingScheduleField.clear();
        maxCapacityField.clear();
        clubMessageLabel.setText("");
        currentClubId = 0;
    }

    private void clearEquipmentTypeFields() {
        equipmentTypeNameField.clear();
        equipmentTypeDescriptionField.clear();
        equipmentTypeTable.getSelectionModel().clearSelection();
        selectedEquipmentType = null;
        equipmentTypeUpdateBtn.setDisable(true);
        equipmentTypeDeleteBtn.setDisable(true);
    }

    private void handleAddSport() {
        try {
            sportController.handleCreateTypeSport(sportNomField.getText(), sportDescField.getText(),
                    Integer.parseInt(sportNbJoueursField.getText()), new ArrayList<>(), new ArrayList<>());
            refreshSportList();
        } catch (Exception e) {
            showError("Erreur", "Saisie invalide.");
        }
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

    private void handleCreateEquipmentType() {
        EquipmentTypeActionResult result = equipmentTypeController.handleCreate(
                equipmentTypeNameField.getText(),
                equipmentTypeDescriptionField.getText()
        );
        showEquipmentTypeMessage(result);
        if (result.isSuccess()) {
            refreshEquipmentTypeList();
            clearEquipmentTypeFields();
        }
    }

    private void handleUpdateEquipmentType() {
        if (selectedEquipmentType == null) {
            showEquipmentTypeMessage(EquipmentTypeActionResult.invalid("Selection requise."));
            return;
        }
        EquipmentTypeActionResult result = equipmentTypeController.handleUpdate(
                selectedEquipmentType,
                equipmentTypeNameField.getText(),
                equipmentTypeDescriptionField.getText()
        );
        showEquipmentTypeMessage(result);
        if (result.isSuccess()) {
            refreshEquipmentTypeList();
            clearEquipmentTypeFields();
        }
    }

    private void handleDeleteEquipmentType() {
        if (selectedEquipmentType == null) {
            showEquipmentTypeMessage(EquipmentTypeActionResult.invalid("Selection requise."));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirmation");
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        EquipmentTypeActionResult result = equipmentTypeController.handleDelete(selectedEquipmentType.getId());
        showEquipmentTypeMessage(result);
        if (result.isSuccess()) {
            refreshEquipmentTypeList();
            clearEquipmentTypeFields();
        }
    }

    private void handleAddMatch() {
        try {
            Match match = buildMatchFromForm(null);
            if (match == null) return;
            Match created = matchController.handleCreateMatch(match);
            if (created == null) {
                matchMessageLabel.setText("Erreur lors de la création du match.");
                return;
            }
            refreshMatchList();
            clearMatchFields();
            matchMessageLabel.setText("");
        } catch (Exception e) {
            matchMessageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void handleUpdateMatch() {
        if (currentMatchId == 0) return;
        try {
            Match match = buildMatchFromForm(currentMatchId);
            if (match == null) return;
            boolean ok = matchController.handleUpdateMatch(match);
            if (!ok) {
                matchMessageLabel.setText("Erreur lors de la mise à jour.");
                return;
            }
            refreshMatchList();
            matchMessageLabel.setText("");
        } catch (Exception e) {
            matchMessageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void refreshSportChoices() {
        List<TypeSport> sports = sportController.handleGetAllTypeSports();
        if (sports == null) {
            clubSportCombo.setItems(FXCollections.observableArrayList());
            return;
        }
        clubSportCombo.setItems(FXCollections.observableArrayList(sports));
    }

    private void refreshMatchChoices() {
        List<TypeSport> sports = sportController.handleGetAllTypeSports();
        matchSportCache = sports == null ? new ArrayList<>() : sports;
        matchSportCombo.setItems(FXCollections.observableArrayList(matchSportCache));

        try {
            matchClubCache = clubController.getAllClubs();
        } catch (SQLException e) {
            matchClubCache = new ArrayList<>();
        }
        matchHomeClubCombo.setItems(FXCollections.observableArrayList(matchClubCache));
        matchAwayClubCombo.setItems(FXCollections.observableArrayList(matchClubCache));
    }

    private void refreshMatchList() {
        List<Match> matches = matchController.handleGetAllMatches();
        if (matches == null) {
            matchTable.setItems(FXCollections.observableArrayList());
            return;
        }
        matchTable.setItems(FXCollections.observableArrayList(matches));
    }

    private void refreshMatchRequestList() {
        List<MatchRequest> requests = matchRequestController.handleGetPendingRequests();
        if (requests == null) {
            matchRequestTable.setItems(FXCollections.observableArrayList());
            return;
        }
        matchRequestCache = requests;
        matchRequestTable.setItems(FXCollections.observableArrayList(requests));
    }

    private void selectClubSport(int sportId) {
        if (clubSportCombo.getItems() == null) {
            return;
        }
        for (TypeSport sport : clubSportCombo.getItems()) {
            if (sport.getId() == sportId) {
                clubSportCombo.getSelectionModel().select(sport);
                return;
            }
        }
        clubSportCombo.getSelectionModel().clearSelection();
    }

    private Match buildMatchFromForm(Integer matchId) {
        TypeSport sport = matchSportCombo.getValue();
        Club home = matchHomeClubCombo.getValue();
        Club away = matchAwayClubCombo.getValue();

        if (sport == null || home == null || away == null) {
            matchMessageLabel.setText("Veuillez sélectionner sport et équipes.");
            return null;
        }
        if (home.getClubID() == away.getClubID()) {
            matchMessageLabel.setText("Les équipes doivent être différentes.");
            return null;
        }
        LocalDate date = matchDatePicker.getValue();
        LocalTime time = parseTime(matchTimeField.getText());
        if (date == null || time == null) {
            matchMessageLabel.setText("Date/heure invalide (HH:mm).");
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        LocalDateTime deadline = null;
        if (matchDeadlineDatePicker.getValue() != null || !matchDeadlineTimeField.getText().isBlank()) {
            LocalDate d = matchDeadlineDatePicker.getValue();
            LocalTime t = parseTime(matchDeadlineTimeField.getText());
            if (d == null || t == null) {
                matchMessageLabel.setText("Deadline invalide (HH:mm).");
                return null;
            }
            deadline = LocalDateTime.of(d, t);
        }

        MatchStatus status = matchStatusCombo.getValue() == null ? MatchStatus.SCHEDULED : matchStatusCombo.getValue();
        Integer homeScore = parseOptionalInteger(matchHomeScoreField.getText());
        Integer awayScore = parseOptionalInteger(matchAwayScoreField.getText());

        return new Match(
                matchId,
                sport.getId(),
                home.getClubID(),
                away.getClubID(),
                dateTime,
                matchLocationField.getText(),
                matchRefereeField.getText(),
                deadline,
                status,
                homeScore,
                awayScore
        );
    }

    private void clearMatchFields() {
        matchSportCombo.getSelectionModel().clearSelection();
        matchHomeClubCombo.getSelectionModel().clearSelection();
        matchAwayClubCombo.getSelectionModel().clearSelection();
        matchDatePicker.setValue(null);
        matchTimeField.clear();
        matchDeadlineDatePicker.setValue(null);
        matchDeadlineTimeField.clear();
        matchLocationField.clear();
        matchRefereeField.clear();
        matchStatusCombo.getSelectionModel().clearSelection();
        matchHomeScoreField.clear();
        matchAwayScoreField.clear();
        matchMessageLabel.setText("");
        currentMatchId = 0;
    }

    private String getClubName(int clubId) {
        for (Club club : matchClubCache) {
            if (club.getClubID() == clubId) {
                return club.getName();
            }
        }
        return String.valueOf(clubId);
    }

    private String getSportName(int sportId) {
        for (TypeSport sport : matchSportCache) {
            if (sport.getId() == sportId) {
                return sport.getNom();
            }
        }
        return String.valueOf(sportId);
    }

    private void selectMatchSport(int sportId) {
        for (TypeSport sport : matchSportCombo.getItems()) {
            if (sport.getId() == sportId) {
                matchSportCombo.getSelectionModel().select(sport);
                return;
            }
        }
        matchSportCombo.getSelectionModel().clearSelection();
    }

    private void selectMatchClub(ComboBox<Club> combo, int clubId) {
        for (Club club : combo.getItems()) {
            if (club.getClubID() == clubId) {
                combo.getSelectionModel().select(club);
                return;
            }
        }
        combo.getSelectionModel().clearSelection();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String formatScore(Match match) {
        if (match.getHomeScore() == null || match.getAwayScore() == null) {
            return "-";
        }
        return match.getHomeScore() + " - " + match.getAwayScore();
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalTime.parse(value.trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isBlank()) return null;
        return Integer.parseInt(value.trim());
    }

    private void handleApproveMatchRequest() {
        MatchRequest selected = matchRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection requise", "Veuillez sélectionner une demande.");
            return;
        }
        boolean ok = matchRequestController.handleApproveRequest(selected.getId());
        if (!ok) {
            showError("Erreur", "Validation impossible.");
            return;
        }
        refreshMatchRequestList();
        refreshMatchList();
    }

    private void handleRejectMatchRequest() {
        MatchRequest selected = matchRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection requise", "Veuillez sélectionner une demande.");
            return;
        }
        boolean ok = matchRequestController.handleRejectRequest(selected.getId());
        if (!ok) {
            showError("Erreur", "Refus impossible.");
            return;
        }
        refreshMatchRequestList();
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(createMenuButtonStyle(active));
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
