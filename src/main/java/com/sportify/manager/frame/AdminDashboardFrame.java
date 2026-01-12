package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.EquipmentTypeController;
import com.sportify.manager.controllers.LicenceController;
import com.sportify.manager.controllers.MatchController;
import com.sportify.manager.controllers.MatchRequestController;
import com.sportify.manager.controllers.TeamController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.EquipmentType;
import com.sportify.manager.services.EquipmentTypeActionResult;
import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchStatus;
import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.Team;
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
    private LicenceController licenceController = new LicenceController();
    private final EquipmentTypeController equipmentTypeController = new EquipmentTypeController();
    private MatchController matchController = MatchController.getInstance();
    private MatchRequestController matchRequestController = MatchRequestController.getInstance();
    private TeamController teamController = TeamController.getInstance();

    // Navigation & Layout
    private StackPane contentArea;
    private VBox clubView, sportView, licenceAdminView, matchView, equipmentTypeView, statView;
    private Button btnClubs, btnSports, btnLicences, btnMatchs, btnTypeEquipment, btnStats;

    // --- ÉLÉMENTS CLUB ---
    private TextField clubNameField, clubDescriptionField, maxCapacityField;
    private TableView<Club> clubTable;
    private ComboBox<User> clubDirectorCombo;
    private Label clubMessageLabel;
    private int currentClubId = 0;
    private List<User> availableDirectors = new ArrayList<>();

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

    // --- ÉLÉMENTS MATCHS ---
    private TableView<Match> matchTable;
    private TableView<MatchRequest> matchRequestTable;
    private ComboBox<TypeSport> matchSportCombo;
    private ComboBox<Team> matchHomeTeamCombo;
    private ComboBox<Team> matchAwayTeamCombo;
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
    private List<Team> matchTeamCache = new ArrayList<>();
    private List<TypeSport> matchSportCache = new ArrayList<>();
    private List<MatchRequest> matchRequestCache = new ArrayList<>();

    // --- ÉLÉMENTS TYPE EQUIPEMENT ---
    private ListView<EquipmentType> equipmentTypeList;
    private TextField equipmentTypeNameField;
    private TextArea equipmentTypeDescField;
    private Label equipmentTypeMessageLabel;

    private ComboBox<Club> statClubCombo;
    private ComboBox<Team> statTeamCombo;
    private Label statMessageLabel;

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

        btnClubs = createMenuButton("Gestion Clubs", true);
        btnSports = createMenuButton("Type de Sports", false);
        btnLicences = createMenuButton("Valider Licences", false);
        btnMatchs = createMenuButton("Gestion Matchs", false);
        btnStats = createMenuButton("Stat Management", false);
        btnTypeEquipment = createMenuButton("Type Equipement", false);
        Button btnLogout = createMenuButton("Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnSports, btnLicences, btnMatchs, btnStats, btnTypeEquipment, btnLogout);

        // --- PRÉPARATION DES VUES ---
        createClubView();
        createSportView();
        createLicenceAdminView();
        createMatchView();
        createStatView();
        createEquipmentTypeView();

        contentArea = new StackPane(clubView, sportView, licenceAdminView, matchView, statView, equipmentTypeView);
        sportView.setVisible(false);
        licenceAdminView.setVisible(false);
        matchView.setVisible(false);
        statView.setVisible(false);
        equipmentTypeView.setVisible(false);

        // --- LOGIQUE DE NAVIGATION ---
        btnClubs.setOnAction(e -> {
            switchView(clubView, btnClubs);
            refreshDirectorChoices(null);
        });
        btnSports.setOnAction(e -> {
            switchView(sportView, btnSports);
            refreshSportList();
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
        btnStats.setOnAction(e -> {
            switchView(statView, btnStats);
            refreshStatClubChoices();
        });
        btnTypeEquipment.setOnAction(e -> {
            switchView(equipmentTypeView, btnTypeEquipment);
            refreshEquipmentTypeList();
        });
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Sportify Admin - Système de Gestion Global");
        primaryStage.setScene(scene);
        refreshClubList();
        refreshDirectorChoices(null);
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
        clubDirectorCombo = new ComboBox<>();
        clubDirectorCombo.setPromptText("Choisir un directeur");
        maxCapacityField = new TextField();
        clubMessageLabel = new Label();
        clubMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        formGrid.add(new Label("Nom du Club :"), 0, 0);
        formGrid.add(clubNameField, 1, 0);
        formGrid.add(new Label("Directeur :"), 2, 0);
        formGrid.add(clubDirectorCombo, 3, 0);
        formGrid.add(new Label("Description :"), 0, 1);
        formGrid.add(clubDescriptionField, 1, 1, 3, 1);
        formGrid.add(new Label("Capacité Max :"), 0, 2);
        formGrid.add(maxCapacityField, 1, 2);

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
        matchHomeTeamCombo = new ComboBox<>();
        matchHomeTeamCombo.setPromptText("Équipe domicile");
        matchAwayTeamCombo = new ComboBox<>();
        matchAwayTeamCombo.setPromptText("Équipe extérieure");
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
        configureMatchTeamCombo(matchHomeTeamCombo);
        configureMatchTeamCombo(matchAwayTeamCombo);
        matchSportCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMatchTeamCombos(newVal));

        formGrid.add(new Label("Sport :"), 0, 0);
        formGrid.add(matchSportCombo, 1, 0);
        formGrid.add(new Label("Date :"), 2, 0);
        formGrid.add(matchDatePicker, 3, 0);

        formGrid.add(new Label("Domicile :"), 0, 1);
        formGrid.add(matchHomeTeamCombo, 1, 1);
        formGrid.add(new Label("Heure :"), 2, 1);
        formGrid.add(matchTimeField, 3, 1);

        formGrid.add(new Label("Extérieur :"), 0, 2);
        formGrid.add(matchAwayTeamCombo, 1, 2);
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

        Button addBtn = new Button("Planifier");
        styleButton(addBtn, "#2ecc71");
        Button updateBtn = new Button("Modifier");
        styleButton(updateBtn, "#f1c40f");
        Button clearBtn = new Button("Vider");
        styleButton(clearBtn, "#95a5a6");

        HBox actions = new HBox(10, addBtn, updateBtn, clearBtn);
        formGrid.add(actions, 1, 6, 3, 1);

        matchTable = new TableView<>();
        setupMatchTable();

        matchRequestTable = new TableView<>();
        setupMatchRequestTable();

        HBox requestActions = new HBox(10);
        Button approveBtn = new Button("Valider");
        styleButton(approveBtn, "#2ecc71");
        Button rejectBtn = new Button("Refuser");
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

    private void createStatView() {
        statView = new VBox(20);
        statView.setPadding(new Insets(30));

        Label title = new Label("Stat Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        statClubCombo = new ComboBox<>();
        statClubCombo.setPromptText("Sélectionner un club");
        statClubCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Club item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        statClubCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Club item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        statClubCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshStatTeamChoices(newVal));

        statTeamCombo = new ComboBox<>();
        statTeamCombo.setPromptText("Sélectionner une équipe");
        statTeamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        statTeamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        Button openBtn = new Button("Ouvrir les stats");
        styleButton(openBtn, "#3498db");
        openBtn.setOnAction(e -> openAdminStats());

        statMessageLabel = new Label();
        statMessageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        statView.getChildren().addAll(title, statClubCombo, statTeamCombo, openBtn, statMessageLabel);
    }

    // ==========================================
    // VUE : TYPE EQUIPEMENT (ADMIN)
    // ==========================================
    private void createEquipmentTypeView() {
        equipmentTypeView = new VBox(20);
        equipmentTypeView.setPadding(new Insets(30));

        Label title = new Label("Gestion des Types d'Équipement");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        equipmentTypeNameField = new TextField();
        equipmentTypeNameField.setPromptText("Nom");
        equipmentTypeDescField = new TextArea();
        equipmentTypeDescField.setPromptText("Description");
        equipmentTypeDescField.setPrefRowCount(3);

        Button btnCreate = new Button("➕ Créer");
        styleButton(btnCreate, "#2ecc71");
        Button btnDelete = new Button("🗑 Supprimer");
        styleButton(btnDelete, "#e74c3c");

        equipmentTypeMessageLabel = new Label();
        equipmentTypeMessageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        equipmentTypeList = new ListView<>();
        equipmentTypeList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(EquipmentType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        btnCreate.setOnAction(e -> handleCreateEquipmentType());
        btnDelete.setOnAction(e -> handleDeleteEquipmentType());

        equipmentTypeView.getChildren().addAll(
                title,
                equipmentTypeNameField,
                equipmentTypeDescField,
                new HBox(10, btnCreate, btnDelete),
                equipmentTypeMessageLabel,
                new Separator(),
                equipmentTypeList
        );
    }

    private void handleCreateEquipmentType() {
        EquipmentTypeActionResult result = equipmentTypeController.handleCreate(
                equipmentTypeNameField.getText(),
                equipmentTypeDescField.getText()
        );
        equipmentTypeMessageLabel.setText(result.getMessage());
        if (result.isSuccess()) {
            refreshEquipmentTypeList();
        }
    }

    private void handleDeleteEquipmentType() {
        EquipmentType selected = equipmentTypeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            equipmentTypeMessageLabel.setText("Sélectionnez un type.");
            return;
        }
        EquipmentTypeActionResult result = equipmentTypeController.handleDelete(selected.getId());
        equipmentTypeMessageLabel.setText(result.getMessage());
        if (result.isSuccess()) {
            refreshEquipmentTypeList();
        }
    }

    private void refreshEquipmentTypeList() {
        List<EquipmentType> list = equipmentTypeController.handleListAll();
        equipmentTypeList.setItems(FXCollections.observableArrayList(list == null ? List.of() : list));
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
        grid.add(new Label("Small events (1/ligne) :"), 2, 2);
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

    // --- LOGIQUE COMMUNE & UTILS ---
    private void switchView(VBox view, Button activeBtn) {
        clubView.setVisible(false);
        sportView.setVisible(false);
        licenceAdminView.setVisible(false);
        matchView.setVisible(false);
        if (statView != null) {
            statView.setVisible(false);
        }
        if (equipmentTypeView != null) {
            equipmentTypeView.setVisible(false);
        }
        view.setVisible(true);

        btnClubs.setStyle(createMenuButtonStyle(btnClubs == activeBtn));
        btnSports.setStyle(createMenuButtonStyle(btnSports == activeBtn));
        btnLicences.setStyle(createMenuButtonStyle(btnLicences == activeBtn));
        btnMatchs.setStyle(createMenuButtonStyle(btnMatchs == activeBtn));
        if (btnStats != null) {
            btnStats.setStyle(createMenuButtonStyle(btnStats == activeBtn));
        }
        if (btnTypeEquipment != null) {
            btnTypeEquipment.setStyle(createMenuButtonStyle(btnTypeEquipment == activeBtn));
        }
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

    private void refreshClubList() {
        try {
            clubTable.setItems(FXCollections.observableArrayList(clubController.getAllClubs()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshSportList() {
        sportTable.setItems(FXCollections.observableArrayList(sportController.handleGetAllTypeSports()));
    }

    private void setupClubTable() {
        TableColumn<Club, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("clubId"));

        TableColumn<Club, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Club, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Club, Integer> capacityCol = new TableColumn<>("Capacité");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("maxCapacity"));

        TableColumn<Club, Integer> membersCol = new TableColumn<>("Membres");
        membersCol.setCellValueFactory(new PropertyValueFactory<>("currentMemberCount"));

        TableColumn<Club, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Club, String> directorCol = new TableColumn<>("Directeur");
        directorCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getDirectorName(cellData.getValue().getManagerId())));

        clubTable.getColumns().addAll(idCol, nameCol, descCol, capacityCol, membersCol, statusCol, directorCol);
        clubTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            if (newSelection != null) {
                currentClubId = newSelection.getClubID();
                clubNameField.setText(newSelection.getName());
                clubDescriptionField.setText(newSelection.getDescription());
                refreshDirectorChoices(newSelection.getManagerId());
                selectClubDirector(newSelection.getManagerId());
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
                sportRolesField.setText(String.join("\n", newVal.getRoles() == null ? List.of() : newVal.getRoles()));
                sportStatsField.setText(String.join("\n", newVal.getStatistiques() == null ? List.of() : newVal.getStatistiques()));
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
                new javafx.beans.property.SimpleStringProperty(getTeamDisplayById(cellData.getValue().getHomeTeamId())));

        TableColumn<Match, String> awayCol = new TableColumn<>("Extérieur");
        awayCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getTeamDisplayById(cellData.getValue().getAwayTeamId())));

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
                selectMatchTeam(matchHomeTeamCombo, selected.getHomeTeamId());
                selectMatchTeam(matchAwayTeamCombo, selected.getAwayTeamId());
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

        TableColumn<MatchRequest, String> requesterCol = new TableColumn<>("Domicile");
        requesterCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getTeamDisplayById(cellData.getValue().getHomeTeamId())));

        TableColumn<MatchRequest, String> opponentCol = new TableColumn<>("Extérieur");
        opponentCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getTeamDisplayById(cellData.getValue().getAwayTeamId())));

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
            User selectedDirector = clubDirectorCombo.getValue();
            if (selectedDirector == null) {
                clubMessageLabel.setText("Veuillez sélectionner un directeur.");
                return;
            }
            clubController.createClub(
                    0,
                    clubNameField.getText(),
                    clubDescriptionField.getText(),
                    0,
                    "",
                    Integer.parseInt(maxCapacityField.getText()),
                    selectedDirector.getId()
            );

            refreshClubList();
            clearClubFields();
            refreshDirectorChoices(null);
            clubMessageLabel.setText("");

        } catch (Exception e) {
            clubMessageLabel.setText("Erreur : " + e.getMessage());
        }
    }





    private void handleUpdateClub() {
        if (currentClubId == 0) return;

        try {
            User selectedDirector = clubDirectorCombo.getValue();
            if (selectedDirector == null) {
                clubMessageLabel.setText("Veuillez sélectionner un directeur.");
                return;
            }
            Club c = new Club(
                    currentClubId,
                    clubNameField.getText(),
                    clubDescriptionField.getText(),
                    0,
                    "",
                    Integer.parseInt(maxCapacityField.getText()),
                    selectedDirector.getId()
            );

            clubController.updateClub(c);

            refreshClubList();
            refreshDirectorChoices(selectedDirector.getId());
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
        maxCapacityField.clear();
        clubDirectorCombo.getSelectionModel().clearSelection();
        clubMessageLabel.setText("");
        currentClubId = 0;
    }

    private void handleAddSport() {
        try {
            List<String> roles = parseLines(sportRolesField.getText());
            List<String> stats = parseLines(sportStatsField.getText());
            sportController.handleCreateTypeSport(sportNomField.getText(), sportDescField.getText(),
                    Integer.parseInt(sportNbJoueursField.getText()), roles, stats);
            refreshSportList();
        } catch (Exception e) {
            showError("Erreur", "Saisie invalide.");
        }
    }

    private void handleUpdateSport() {
        if (selectedTypeSport == null) return;
        selectedTypeSport.setNom(sportNomField.getText());
        selectedTypeSport.setDescription(sportDescField.getText());
        selectedTypeSport.setNbJoueurs(Integer.parseInt(sportNbJoueursField.getText()));
        selectedTypeSport.setRoles(parseLines(sportRolesField.getText()));
        selectedTypeSport.setStatistiques(parseLines(sportStatsField.getText()));
        sportController.handleUpdateTypeSport(selectedTypeSport);
        refreshSportList();
    }

    private void handleDeleteSport() {
        if (selectedTypeSport == null) return;
        sportController.handleDeleteTypeSport(selectedTypeSport.getId());
        refreshSportList();
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

    private void refreshMatchChoices() {
        List<TypeSport> sports = sportController.handleGetAllTypeSports();
        matchSportCache = sports == null ? new ArrayList<>() : sports;
        matchSportCombo.setItems(FXCollections.observableArrayList(matchSportCache));

        try {
            matchClubCache = clubController.getAllClubs();
        } catch (SQLException e) {
            matchClubCache = new ArrayList<>();
        }
        matchTeamCache = new ArrayList<>();
        for (Club club : matchClubCache) {
            List<Team> teams = teamController.handleGetTeams(club.getClubID());
            if (teams != null) {
                matchTeamCache.addAll(teams);
            }
        }
        updateMatchTeamCombos(matchSportCombo.getValue());
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

    private void refreshDirectorChoices(String currentDirectorId) {
        try {
            availableDirectors = PostgresUserDAO.getInstance().getDirectorsWithoutClub();
            if (currentDirectorId != null && !currentDirectorId.isBlank()) {
                boolean alreadyListed = availableDirectors.stream()
                        .anyMatch(user -> currentDirectorId.equals(user.getId()));
                if (!alreadyListed) {
                    User current = PostgresUserDAO.getInstance().getUserById(currentDirectorId);
                    if (current != null) {
                        availableDirectors.add(current);
                    }
                }
            }
            clubDirectorCombo.setItems(FXCollections.observableArrayList(availableDirectors));
        } catch (SQLException e) {
            clubDirectorCombo.setItems(FXCollections.observableArrayList());
        }
        clubDirectorCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getId() + ")");
                }
            }
        });
        clubDirectorCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getId() + ")");
                }
            }
        });
    }

    private void refreshStatClubChoices() {
        List<Club> clubs;
        try {
            clubs = clubController.getAllClubs();
        } catch (SQLException e) {
            clubs = new ArrayList<>();
        }
        statClubCombo.setItems(FXCollections.observableArrayList(clubs));
        if (!statClubCombo.getItems().isEmpty()) {
            statClubCombo.getSelectionModel().selectFirst();
            refreshStatTeamChoices(statClubCombo.getValue());
        } else {
            statTeamCombo.setItems(FXCollections.observableArrayList());
        }
        if (statMessageLabel != null) {
            statMessageLabel.setText("");
        }
    }

    private void refreshStatTeamChoices(Club club) {
        if (club == null) {
            statTeamCombo.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Team> teams = teamController.handleGetTeams(club.getClubID());
        statTeamCombo.setItems(FXCollections.observableArrayList(teams == null ? List.of() : teams));
        if (!statTeamCombo.getItems().isEmpty()) {
            statTeamCombo.getSelectionModel().selectFirst();
        }
    }

    private void openAdminStats() {
        Club club = statClubCombo.getValue();
        if (club == null) {
            showError("Erreur", "Sélectionnez un club.");
            return;
        }
        Team team = statTeamCombo.getValue();
        if (team == null) {
            showError("Aucune équipe", "Sélectionnez une équipe.");
            return;
        }
        StatFrame statFrame = new StatFrame();
        statFrame.show(List.of(team));
    }

    private List<String> parseLines(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        String[] parts = value.split("\\R");
        List<String> out = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private void selectClubDirector(String directorId) {
        if (directorId == null || clubDirectorCombo.getItems() == null) {
            clubDirectorCombo.getSelectionModel().clearSelection();
            return;
        }
        for (User user : clubDirectorCombo.getItems()) {
            if (directorId.equals(user.getId())) {
                clubDirectorCombo.getSelectionModel().select(user);
                return;
            }
        }
        clubDirectorCombo.getSelectionModel().clearSelection();
    }

    private String getDirectorName(String directorId) {
        if (directorId == null || directorId.isBlank()) {
            return "";
        }
        User user = PostgresUserDAO.getInstance().getUserById(directorId);
        return user == null ? directorId : user.getName();
    }

    private Match buildMatchFromForm(Integer matchId) {
        TypeSport sport = matchSportCombo.getValue();
        Team home = matchHomeTeamCombo.getValue();
        Team away = matchAwayTeamCombo.getValue();

        if (sport == null || home == null || away == null) {
            matchMessageLabel.setText("Veuillez sélectionner sport et équipes.");
            return null;
        }
        if (home.getId() == away.getId()) {
            matchMessageLabel.setText("Les équipes doivent être différentes.");
            return null;
        }
        Integer homeSportId = home.getTypeSportId();
        Integer awaySportId = away.getTypeSportId();
        if (homeSportId != null && awaySportId != null && !homeSportId.equals(awaySportId)) {
            matchMessageLabel.setText("Les équipes doivent partager le même sport.");
            return null;
        }
        if (!teamMatchesSport(home, sport) || !teamMatchesSport(away, sport)) {
            matchMessageLabel.setText("Les équipes doivent correspondre au sport sélectionné.");
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
                home.getId(),
                away.getId(),
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
        matchHomeTeamCombo.getSelectionModel().clearSelection();
        matchAwayTeamCombo.getSelectionModel().clearSelection();
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

    private void selectMatchTeam(ComboBox<Team> combo, int teamId) {
        for (Team team : combo.getItems()) {
            if (team.getId() == teamId) {
                combo.getSelectionModel().select(team);
                return;
            }
        }
        combo.getSelectionModel().clearSelection();
    }

    private void configureMatchTeamCombo(ComboBox<Team> combo) {
        combo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : getTeamDisplay(item));
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : getTeamDisplay(item));
            }
        });
    }

    private void updateMatchTeamCombos(TypeSport sport) {
        List<Team> filtered = new ArrayList<>();
        for (Team team : matchTeamCache) {
            if (sport == null || teamMatchesSport(team, sport)) {
                filtered.add(team);
            }
        }
        matchHomeTeamCombo.setItems(FXCollections.observableArrayList(filtered));
        matchAwayTeamCombo.setItems(FXCollections.observableArrayList(filtered));
        if (!filtered.contains(matchHomeTeamCombo.getValue())) {
            matchHomeTeamCombo.getSelectionModel().clearSelection();
        }
        if (!filtered.contains(matchAwayTeamCombo.getValue())) {
            matchAwayTeamCombo.getSelectionModel().clearSelection();
        }
    }

    private boolean teamMatchesSport(Team team, TypeSport sport) {
        if (team == null || sport == null) {
            return false;
        }
        if (team.getTypeSportId() == null) {
            return true;
        }
        return team.getTypeSportId() == sport.getId();
    }

    private String getTeamDisplay(Team team) {
        if (team == null) {
            return "";
        }
        String clubName = getClubName(team.getClubId());
        return team.getNom() + " (" + clubName + ")";
    }

    private String getTeamDisplayById(int teamId) {
        for (Team team : matchTeamCache) {
            if (team.getId() == teamId) {
                return getTeamDisplay(team);
            }
        }
        return String.valueOf(teamId);
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
