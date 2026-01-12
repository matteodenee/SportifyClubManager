package com.sportify.manager.frame;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.CompositionController;
import com.sportify.manager.controllers.LicenceController;
import com.sportify.manager.controllers.MatchController;
import com.sportify.manager.controllers.MatchRequestController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.controllers.TrainingController;
import com.sportify.manager.controllers.TeamController;
import com.sportify.manager.controllers.EventController;
import com.sportify.manager.controllers.EquipmentController;
import com.sportify.manager.controllers.EquipmentTypeController;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.facade.TypeSportFacade;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.Composition;
import com.sportify.manager.services.Equipment;
import com.sportify.manager.services.EquipmentType;
import com.sportify.manager.services.EquipmentTypeActionResult;
import com.sportify.manager.services.Event;
import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.MatchRequestStatus;
import com.sportify.manager.services.Reservation;
import com.sportify.manager.services.RoleAssignment;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.Training;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.User;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.dao.PostgresUserDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoachDashboardFrame extends Application {
    private User currentCoach;
    private int coachClubId;
    private Club coachClub;

    private ClubController clubController;
    private LicenceController licenceController;
    private MatchController matchController;
    private MatchRequestController matchRequestController;
    private CompositionController compositionController;
    private TypeSportController sportController;
    private TrainingController trainingController;
    private TeamController teamController = TeamController.getInstance();
    private EventController eventController = EventController.getInstance();
    private EquipmentController equipmentController = new EquipmentController();
    private EquipmentTypeController equipmentTypeController = new EquipmentTypeController();

    private StackPane displayArea;

    // Team Management (Coach)
    private VBox teamManagementView;
    private TableView<Team> coachTeamTable;
    private TableView<User> coachTeamPlayersTable;
    private TextField coachPlayerIdField;
    private TextField playerSearchField;
    private ListView<User> playerSearchResults;
    private Label teamStatsLabel;

    // Match View
    private VBox matchView;
    private VBox licenceView;
    private TableView<Match> matchTable;
    private TableView<MatchRequest> matchRequestTable;
    private TextField matchRequestSearchField;
    private TextField matchSearchField;
    private Label matchRequestCountLabel;
    private Label matchCountLabel;
    private Label lastRefreshLabel;
    private java.util.List<MatchRequest> matchRequestCache = new java.util.ArrayList<>();
    private java.util.List<Match> matchCache = new java.util.ArrayList<>();
    private ComboBox<Team> coachTeamCombo;
    private ComboBox<Team> opponentTeamCombo;
    private ComboBox<String> venueCombo;
    private DatePicker matchDatePicker;
    private TextField matchTimeField;
    private TextField matchLocationField;
    private TextField matchRefereeField;
    private Label matchMessageLabel;
    private List<Club> clubCache = new ArrayList<>();
    private List<Team> matchTeamCache = new ArrayList<>();
    private Timeline matchRefreshTimeline;
    private VBox licenceStatusBox;
    private boolean documentAttached;

    // Training View
    private VBox trainingView;
    private VBox eventView;
    private VBox communicationView;
    private VBox equipmentView;
    private VBox typeEquipmentView;
    private DatePicker trainingDatePicker;
    private TextField trainingTimeField;
    private TextField trainingLocationField;
    private TextField trainingActivityField;
    private TextField trainingClubIdField;  // ← Cette ligne est importante
    private ComboBox<Team> trainingTeamCombo;
    private ComboBox<Team> trainingFilterTeamCombo;
    private TableView<Training> trainingTable;
    private DatePicker trainingFromDatePicker;
    private Label trainingMessageLabel;
    private Map<Integer, String> trainingTeamNames = new HashMap<>();

    // Event View
    private ListView<Event> eventList;
    private TextField eventNameField;
    private TextArea eventDescriptionField;
    private DatePicker eventDatePicker;
    private TextField eventTimeField;
    private TextField eventDurationField;
    private TextField eventLocationField;
    private TextField eventTypeField;
    private ChoiceBox<String> eventRsvpChoice;
    private Label eventMessageLabel;

    // Equipment View
    private ListView<Equipment> equipmentList;
    private ListView<Reservation> equipmentReservationList;
    private DatePicker equipmentStartDate;
    private DatePicker equipmentEndDate;
    private Label equipmentMessageLabel;

    // Type Equipment View
    private ListView<EquipmentType> equipmentTypeList;
    private TextField equipmentTypeNameField;
    private TextArea equipmentTypeDescField;
    private Label equipmentTypeMessageLabel;

    public CoachDashboardFrame(User user) {
        this.currentCoach = user;
        this.coachClubId = PostgresUserDAO.getInstance().getClubIdByCoach(user.getId());
    }

    @Override
    public void start(Stage primaryStage) {
        Connection connection = PostgresUserDAO.getConnection();

        this.clubController = new ClubController(connection);
        this.licenceController = new LicenceController();
        this.licenceController.setCurrentUser(currentCoach);
        this.matchController = MatchController.getInstance();
        this.matchRequestController = MatchRequestController.getInstance();
        this.compositionController = CompositionController.getInstance();
        this.sportController = new TypeSportController();
        this.trainingController = new TrainingController();
        this.trainingController.setCoachDashboard(this);

        loadCoachClub();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f7f6;");

        // --- SIDEBAR ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("MENU COACH");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnTeam = createMenuButton("Team Management");
        Button btnTraining = createMenuButton("Training Management");
        Button btnMatch = createMenuButton("Match Management");
        Button btnLicence = createMenuButton("Ma Licence");
        Button btnStats = createMenuButton("Stat Management");
        Button btnEvents = createMenuButton("Event Management");
        Button btnCommunication = createMenuButton("Communication Management");
        Button btnEquipment = createMenuButton("Equipement Management");
        Button btnLogout = createMenuButton("Déconnexion");

        sidebar.getChildren().addAll(
                menuLabel,
                new Separator(),
                btnTeam,
                btnTraining,
                btnMatch,
                btnLicence,
                btnStats,
                btnEvents,
                btnCommunication,
                btnEquipment,
                btnLogout
        );

        // --- MAIN CONTENT AREA ---
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));

        Label welcomeLabel = new Label("Tableau de bord : " + currentCoach.getName());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label clubInfo = new Label("Club assigné ID : " + (coachClubId != -1 ? coachClubId : "Aucun club trouvé"));
        clubInfo.setStyle("-fx-text-fill: #7f8c8d;");

        displayArea = new StackPane();
        displayArea.setPrefHeight(400);
        displayArea.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label placeholderText = new Label("Sélectionnez une fonctionnalité dans le menu de gauche.");
        displayArea.getChildren().add(placeholderText);

        createMatchView();
        createLicenceView();
        createTrainingView();
        createEventView();
        createCommunicationView();
        createEquipmentView();
        createTypeEquipmentView();
        createTeamManagementView();

        // --- ACTIONS ---
        btnStats.setOnAction(e -> {
            if (coachClubId != -1) {
                List<Team> myTeams = getCoachTeams();
                if (myTeams.isEmpty()) {
                    showError("Erreur", "Aucune équipe assignée à ce coach.");
                    return;
                }
                StatFrame statFrame = new StatFrame();
                statFrame.show(myTeams);
            } else {
                placeholderText.setText("Erreur : Aucun club n'est associé à votre compte coach.");
            }
        });

        btnTeam.setOnAction(e -> {
            setDisplay(teamManagementView);
            refreshCoachTeams();
        });
        btnTraining.setOnAction(e -> {
            if (coachClubId == -1) {
                showError("Erreur", "Aucun club n'est associé à votre compte coach.");
                return;
            }
            refreshTrainingTeams();
            setDisplay(trainingView);
            if (trainingFromDatePicker != null) {
                trainingFromDatePicker.setValue(LocalDate.now());
            }
            refreshTrainingList();
        });
        btnEvents.setOnAction(e -> {
            setDisplay(eventView);
            refreshEventList();
        });
        btnCommunication.setOnAction(e -> setDisplay(communicationView));
        btnEquipment.setOnAction(e -> {
            setDisplay(equipmentView);
            refreshEquipmentList();
        });
        btnMatch.setOnAction(e -> {
            if (coachClubId == -1 || coachClub == null) {
                showError("Erreur", "Aucun club n'est associé à votre compte coach.");
                return;
            }
            startMatchAutoRefresh();
            setDisplay(matchView);
        });
        btnLicence.setOnAction(e -> {
            setDisplay(licenceView);
            refreshLicenceInfo();
        });
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        setDisplay(teamManagementView);
        refreshCoachTeams();

        mainContent.getChildren().addAll(welcomeLabel, clubInfo, displayArea);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Sportify - Coach Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================
    // TEAM MANAGEMENT (COACH)
    // ==========================================
    private void createTeamManagementView() {
        teamManagementView = new VBox(15);
        teamManagementView.setPadding(new Insets(20));

        Label title = new Label("Team Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        teamStatsLabel = new Label("Chargement...");
        teamStatsLabel.setStyle("-fx-text-fill: #7f8c8d;");


        // === TABLE DES ÉQUIPES - CONFIGURATION CORRIGÉE ===
        coachTeamTable = new TableView<>();
        coachTeamTable.setPrefHeight(200);
        coachTeamTable.setMinHeight(150);

        // Colonne ID
        TableColumn<Team, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMinWidth(50);

        // Colonne Nom
        TableColumn<Team, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nameCol.setMinWidth(150);

        // Colonne Catégorie
        TableColumn<Team, String> catCol = new TableColumn<>("Catégorie");
        catCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        catCol.setMinWidth(120);

        // Colonne Coach ID (pour debug)
        TableColumn<Team, String> coachCol = new TableColumn<>("Coach");
        coachCol.setCellValueFactory(cellData -> {
            String coachId = cellData.getValue().getCoachId();
            return new javafx.beans.property.SimpleStringProperty(coachId != null ? coachId : "Non assigné");
        });
        coachCol.setMinWidth(100);

        coachTeamTable.getColumns().addAll(idCol, nameCol, catCol, coachCol);
        coachTeamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        coachTeamTable.setPlaceholder(new Label("Aucune équipe à afficher"));

        // Listener pour charger les joueurs
        coachTeamTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            refreshCoachTeamPlayers(sel);
        });

        // === TABLE DES JOUEURS ===
        coachTeamPlayersTable = new TableView<>();
        coachTeamPlayersTable.setPrefHeight(200);
        coachTeamPlayersTable.setMinHeight(150);

        TableColumn<User, String> pidCol = new TableColumn<>("ID");
        pidCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        pidCol.setMinWidth(100);

        TableColumn<User, String> pnameCol = new TableColumn<>("Nom");
        pnameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pnameCol.setMinWidth(200);

        coachTeamPlayersTable.getColumns().addAll(pidCol, pnameCol);
        coachTeamPlayersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        coachTeamPlayersTable.setPlaceholder(new Label("Sélectionnez une équipe"));

        // === ACTIONS JOUEURS ===
        coachPlayerIdField = new TextField();
        coachPlayerIdField.setPromptText("User ID");

        Button addBtn = new Button("➕ Ajouter joueur");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> handleCoachAddPlayer());

        Button removeBtn = new Button("➖ Retirer joueur");
        removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        removeBtn.setOnAction(e -> handleCoachRemovePlayer());

        // === RECHERCHE JOUEUR ===
        playerSearchField = new TextField();
        playerSearchField.setPromptText("Rechercher par nom");

        Button searchBtn = new Button("🔍 Rechercher");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        searchBtn.setOnAction(e -> handlePlayerSearch());

        playerSearchResults = new ListView<>();
        playerSearchResults.setPrefHeight(150);
        playerSearchResults.setCellFactory(list -> new ListCell<>() {
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

        playerSearchResults.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                coachPlayerIdField.setText(sel.getId());
            }
        });

        HBox playerActions = new HBox(10, coachPlayerIdField, addBtn, removeBtn);
        playerActions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox searchBox = new HBox(10, playerSearchField, searchBtn);
        searchBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // === ASSEMBLY ===
        teamManagementView.getChildren().addAll(
                title,
                new HBox(10, teamStatsLabel),
                new Separator(),
                new Label("Mes équipes"),
                coachTeamTable,
                new Separator(),
                new Label("Joueurs de l'équipe sélectionnée"),
                coachTeamPlayersTable,
                playerActions,
                new Separator(),
                new Label("Rechercher un joueur dans le club"),
                searchBox,
                playerSearchResults
        );

    }
    private void refreshCoachTeams() {
        if (coachClubId <= 0) {
            coachTeamTable.setItems(FXCollections.observableArrayList());
            coachTeamPlayersTable.setItems(FXCollections.observableArrayList());
            teamStatsLabel.setText("Aucun club assigné");
            return;
        }

        List<Team> allTeams = teamController.handleGetTeams(coachClubId);

        if (allTeams == null) {
            coachTeamTable.setItems(FXCollections.observableArrayList());
            coachTeamPlayersTable.setItems(FXCollections.observableArrayList());
            teamStatsLabel.setText(" Erreur de chargement");
            return;
        }

        List<Team> myTeams = new ArrayList<>();
        for (Team team : allTeams) {
            if (team != null && team.getCoachId() != null) {
                boolean isMyTeam = team.getCoachId().equals(currentCoach.getId());
                if (isMyTeam) {
                    myTeams.add(team);
                }
            }
        }

        coachTeamTable.setItems(FXCollections.observableArrayList(myTeams));
        coachTeamTable.refresh();  // Force refresh

        // Mettre à jour les stats
        updateTeamStats(myTeams);

        // Message utilisateur
        if (myTeams.isEmpty()) {
            teamStatsLabel.setText("⚠️ Aucune équipe assignée à ce coach");
            teamStatsLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        }

        // Vider la table des joueurs
        coachTeamPlayersTable.setItems(FXCollections.observableArrayList());
    }

    private List<Team> getCoachTeams() {
        if (coachClubId <= 0) {
            return new ArrayList<>();
        }
        List<Team> allTeams = teamController.handleGetTeams(coachClubId);
        if (allTeams == null) {
            return new ArrayList<>();
        }
        List<Team> myTeams = new ArrayList<>();
        for (Team team : allTeams) {
            if (team != null && currentCoach.getId().equals(team.getCoachId())) {
                myTeams.add(team);
            }
        }
        return myTeams;
    }



    private void refreshCoachTeamPlayers(Team team) {
        if (team == null) {
            coachTeamPlayersTable.setItems(FXCollections.observableArrayList());
            return;
        }
        List<User> players = teamController.handleGetPlayers(team.getId());
        coachTeamPlayersTable.setItems(FXCollections.observableArrayList(players == null ? List.of() : players));
    }

    private void updateTeamStats(List<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            teamStatsLabel.setText("Aucune équipe");
            return;
        }

        int teamCount = teams.size();
        int totalPlayers = 0;

        for (Team t : teams) {
            if (t != null) {
                List<User> players = teamController.handleGetPlayers(t.getId());
                totalPlayers += (players != null ? players.size() : 0);
            }
        }

        teamStatsLabel.setText("Équipes: " + teamCount + " | Joueurs: " + totalPlayers);
        teamStatsLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

    }

    private void handleCoachAddPlayer() {
        Team team = coachTeamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            showError("Sélection requise", "Sélectionnez une équipe.");
            return;
        }
        String userId = coachPlayerIdField.getText();
        if (userId == null || userId.isBlank()) {
            showError("Entrée invalide", "User ID requis.");
            return;
        }
        boolean ok = teamController.handleAddPlayer(team.getId(), userId.trim());
        if (ok) {
            refreshCoachTeamPlayers(team);
            refreshCoachTeams();
        } else {
            showError("Erreur", teamController.getLastError());
        }
    }

    private void handleCoachRemovePlayer() {
        Team team = coachTeamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            showError("Sélection requise", "Sélectionnez une équipe.");
            return;
        }
        User player = coachTeamPlayersTable.getSelectionModel().getSelectedItem();
        if (player == null) {
            showError("Sélection requise", "Sélectionnez un joueur.");
            return;
        }
        boolean ok = teamController.handleRemovePlayer(team.getId(), player.getId());
        if (ok) {
            refreshCoachTeamPlayers(team);
            refreshCoachTeams();
        } else {
            showError("Erreur", teamController.getLastError());
        }
    }

    private void handlePlayerSearch() {
        String query = playerSearchField.getText();
        if (query == null || query.isBlank()) {
            playerSearchResults.setItems(FXCollections.observableArrayList());
            return;
        }
        List<User> results = PostgresUserDAO.getInstance().searchMembersByName(coachClubId, query);
        playerSearchResults.setItems(FXCollections.observableArrayList(results == null ? List.of() : results));
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT;");
        });

        return btn;
    }

    // ==========================================
    // TRAINING MANAGEMENT
    // ==========================================
    private void createTrainingView() {
        trainingView = new VBox(15);
        trainingView.setPadding(new Insets(20));

        Label title = new Label("Gestion des Entraînements");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // === FORMULAIRE DE PLANIFICATION ===
        Label planLabel = new Label("Planifier un entraînement");
        planLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(8);
        formGrid.setPadding(new Insets(10));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dcdde1;");

        trainingDatePicker = new DatePicker();
        trainingTimeField = new TextField();
        trainingTimeField.setPromptText("18:30");
        trainingLocationField = new TextField();
        trainingLocationField.setPromptText("Terrain principal");
        trainingActivityField = new TextField();
        trainingActivityField.setPromptText("Endurance, technique...");

        trainingTeamCombo = new ComboBox<>();
        trainingTeamCombo.setPromptText("Équipe");
        trainingTeamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (ID " + item.getId() + ")");
                }
            }
        });
        trainingTeamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (ID " + item.getId() + ")");
                }
            }
        });

        trainingClubIdField = new TextField();
        trainingClubIdField.setText(String.valueOf(coachClubId));
        trainingClubIdField.setDisable(true);

        formGrid.add(new Label("Date :"), 0, 0);
        formGrid.add(trainingDatePicker, 1, 0);
        formGrid.add(new Label("Heure (HH:mm) :"), 2, 0);
        formGrid.add(trainingTimeField, 3, 0);

        formGrid.add(new Label("Lieu :"), 0, 1);
        formGrid.add(trainingLocationField, 1, 1);
        formGrid.add(new Label("Activité :"), 2, 1);
        formGrid.add(trainingActivityField, 3, 1);

        formGrid.add(new Label("Équipe :"), 0, 2);
        formGrid.add(trainingTeamCombo, 1, 2);

        Button createButton = new Button("➕ Planifier");
        createButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        createButton.setOnAction(e -> handleCreateTraining());

        Button updateButton = new Button("✏️ Modifier");
        updateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(e -> handleUpdateTraining());

        Button deleteButton = new Button("🗑️ Supprimer");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> handleDeleteTraining());

        HBox createRow = new HBox(10, createButton, updateButton, deleteButton);
        createRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // === LISTE DES ENTRAÎNEMENTS ===
        Label upcomingLabel = new Label("Entraînements à venir");
        upcomingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label filterLabel = new Label("Équipe :");
        trainingFilterTeamCombo = new ComboBox<>();
        trainingFilterTeamCombo.setPromptText("Toutes les équipes");
        trainingFilterTeamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (ID " + item.getId() + ")");
                }
            }
        });
        trainingFilterTeamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (ID " + item.getId() + ")");
                }
            }
        });
        trainingFilterTeamCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshTrainingList());
        filterRow.getChildren().addAll(filterLabel, trainingFilterTeamCombo);

        trainingTable = new TableView<>();
        trainingTable.setPrefHeight(220);
        trainingTable.setMinHeight(180);
        setupTrainingTable();

        trainingTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> loadTrainingSelection(newVal)
        );

        trainingMessageLabel = new Label();
        trainingMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        // === ASSEMBLY ===
        trainingView.getChildren().addAll(
                title,
                new Separator(),
                planLabel,
                formGrid,
                createRow,
                new Separator(),
                upcomingLabel,
                filterRow,
                trainingTable,
                trainingMessageLabel
        );
    }

    private void refreshTrainingTeams() {
        if (coachClubId <= 0) {
            trainingTeamCombo.setItems(FXCollections.observableArrayList());
            if (trainingFilterTeamCombo != null) {
                trainingFilterTeamCombo.setItems(FXCollections.observableArrayList());
                trainingFilterTeamCombo.getSelectionModel().clearSelection();
            }
            trainingTeamNames.clear();
            return;
        }
        List<Team> teams = teamController.handleGetTeams(coachClubId);
        if (teams != null) {
            teams = teams.stream()
                    .filter(t -> t != null && currentCoach.getId().equals(t.getCoachId()))
                    .toList();
        }
        trainingTeamNames.clear();
        if (teams != null) {
            for (Team team : teams) {
                if (team != null) {
                    trainingTeamNames.put(team.getId(), team.getNom());
                }
            }
        }
        trainingTeamCombo.setItems(FXCollections.observableArrayList(teams == null ? List.of() : teams));
        if (trainingTeamCombo.getSelectionModel().isEmpty() && !trainingTeamCombo.getItems().isEmpty()) {
            trainingTeamCombo.getSelectionModel().selectFirst();
        }
        if (trainingFilterTeamCombo != null) {
            trainingFilterTeamCombo.setItems(FXCollections.observableArrayList(teams == null ? List.of() : teams));
            if (trainingFilterTeamCombo.getItems().size() == 1) {
                trainingFilterTeamCombo.getSelectionModel().selectFirst();
            } else {
                trainingFilterTeamCombo.getSelectionModel().clearSelection();
            }
        }
    }

    private void setupTrainingTable() {
        trainingTable.getColumns().clear();

        TableColumn<Training, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMinWidth(50);

        TableColumn<Training, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setMinWidth(100);

        TableColumn<Training, LocalTime> timeCol = new TableColumn<>("Heure");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("heure"));
        timeCol.setMinWidth(80);

        TableColumn<Training, String> locationCol = new TableColumn<>("Lieu");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        locationCol.setMinWidth(150);

        TableColumn<Training, String> activityCol = new TableColumn<>("Activité");
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activite"));
        activityCol.setMinWidth(150);

        TableColumn<Training, String> teamCol = new TableColumn<>("Équipe");
        teamCol.setCellValueFactory(cell -> new SimpleStringProperty(getTeamName(cell.getValue().getTeamId())));
        teamCol.setMinWidth(120);

        trainingTable.getColumns().addAll(idCol, dateCol, timeCol, teamCol, locationCol, activityCol);
        trainingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trainingTable.setPlaceholder(new Label("Aucun entraînement à afficher"));
    }

    private void handleCreateTraining() {
        trainingController.onCreate();
    }

    private void refreshTrainingList() {
        trainingController.onRefresh();
    }

    private void handleUpdateTraining() {
        trainingController.onUpdate();
    }

    private void handleDeleteTraining() {
        trainingController.onDelete();
    }

    // Méthodes accesseurs pour TrainingController (similaires à TrainingFrame)
    public LocalDate getTrainingDate() {
        return trainingDatePicker != null ? trainingDatePicker.getValue() : null;
    }

    public String getTrainingTime() {
        return trainingTimeField != null ? trainingTimeField.getText() : "";
    }

    public String getTrainingLocation() {
        return trainingLocationField != null ? trainingLocationField.getText() : "";
    }

    public String getTrainingActivity() {
        return trainingActivityField != null ? trainingActivityField.getText() : "";
    }

    public String getClubId() {
        return trainingClubIdField != null ? trainingClubIdField.getText() : "";
    }

    public int getTrainingTeamId() {
        Team team = trainingTeamCombo != null ? trainingTeamCombo.getValue() : null;
        return team != null ? team.getId() : -1;
    }

    public int getTrainingFilterTeamId() {
        Team team = trainingFilterTeamCombo != null ? trainingFilterTeamCombo.getValue() : null;
        return team != null ? team.getId() : -1;
    }

    public LocalDate getFromDate() {
        return null;
    }

    public int getSelectedTrainingId() {
        if (trainingTable == null || trainingTable.getSelectionModel().getSelectedItem() == null) {
            return -1;
        }
        Training selected = trainingTable.getSelectionModel().getSelectedItem();
        return selected.getId();
    }

    public void setTrainings(List<Training> trainings) {
        if (trainingTable == null) {
            return;
        }
        List<Training> safeList = trainings == null ? List.of() : trainings;
        trainingTable.getSelectionModel().clearSelection();
        trainingTable.setItems(FXCollections.observableArrayList(safeList));
        trainingTable.refresh();
    }

    private String getTeamName(int teamId) {
        String name = trainingTeamNames.get(teamId);
        return name != null ? name : ("ID " + teamId);
    }

    public void showTrainingSuccess(String message) {
        if (trainingMessageLabel != null) {
            trainingMessageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            trainingMessageLabel.setText(message);
        }
    }

    public void showTrainingError(String message) {
        if (trainingMessageLabel != null) {
            trainingMessageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            trainingMessageLabel.setText(message);
        }
    }

    public void clearTrainingForm() {
        if (trainingDatePicker != null) trainingDatePicker.setValue(null);
        if (trainingTimeField != null) trainingTimeField.clear();
        if (trainingLocationField != null) trainingLocationField.clear();
        if (trainingActivityField != null) trainingActivityField.clear();
    }

    public void resetTrainingFilter() {}

    private void loadTrainingSelection(Training training) {
        if (training == null) {
            return;
        }
        if (trainingDatePicker != null) trainingDatePicker.setValue(training.getDate());
        if (trainingTimeField != null) trainingTimeField.setText(training.getHeure() != null ? training.getHeure().toString() : "");
        if (trainingLocationField != null) trainingLocationField.setText(training.getLieu() != null ? training.getLieu() : "");
        if (trainingActivityField != null) trainingActivityField.setText(training.getActivite() != null ? training.getActivite() : "");
        if (trainingTeamCombo != null) {
            Team match = null;
            for (Team team : trainingTeamCombo.getItems()) {
                if (team != null && team.getId() == training.getTeamId()) {
                    match = team;
                    break;
                }
            }
            if (match != null) {
                trainingTeamCombo.getSelectionModel().select(match);
            }
        }
    }

    // ==========================================
    // MATCHS COACH
    // ==========================================
    private void createMatchView() {
        matchView = new VBox(20);
        matchView.setPadding(new Insets(20));

        Label title = new Label("Matchs des équipes");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // === FORMULAIRE ===
        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(15));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dcdde1;");

        coachTeamCombo = new ComboBox<>();
        coachTeamCombo.setPromptText("Mon équipe");
        coachTeamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (" + getClubName(item.getClubId()) + ")");
                }
            }
        });
        coachTeamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (" + getClubName(item.getClubId()) + ")");
                }
            }
        });

        opponentTeamCombo = new ComboBox<>();
        opponentTeamCombo.setPromptText("Équipe adverse");
        opponentTeamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (" + getClubName(item.getClubId()) + ")");
                }
            }
        });
        opponentTeamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (" + getClubName(item.getClubId()) + ")");
                }
            }
        });

        venueCombo = new ComboBox<>();
        venueCombo.setItems(javafx.collections.FXCollections.observableArrayList("Domicile", "Extérieur"));
        venueCombo.getSelectionModel().selectFirst();

        matchDatePicker = new DatePicker();

        matchTimeField = new TextField();
        matchTimeField.setPromptText("HH:mm");

        matchLocationField = new TextField();
        matchLocationField.setPromptText("Ex: Stade Municipal");

        matchRefereeField = new TextField();
        matchRefereeField.setPromptText("Nom de l'arbitre");

        formGrid.add(new Label("Mon équipe :"), 0, 0);
        formGrid.add(coachTeamCombo, 1, 0);
        formGrid.add(new Label("Adversaire :"), 2, 0);
        formGrid.add(opponentTeamCombo, 3, 0);

        formGrid.add(new Label("Lieu :"), 0, 1);
        formGrid.add(venueCombo, 1, 1);
        formGrid.add(new Label("Date :"), 2, 1);
        formGrid.add(matchDatePicker, 3, 1);

        formGrid.add(new Label("Heure :"), 0, 2);
        formGrid.add(matchTimeField, 1, 2);
        formGrid.add(new Label("Localisation :"), 2, 2);
        formGrid.add(matchLocationField, 3, 2);

        formGrid.add(new Label("Arbitre :"), 0, 3);
        formGrid.add(matchRefereeField, 1, 3);

        Button btnCreate = new Button("➕ Demander");
        btnCreate.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnCompose = new Button("📋 Composer équipe");
        btnCompose.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox actions = new HBox(10, btnCreate, btnCompose);
        formGrid.add(actions, 1, 4, 3, 1);

        matchMessageLabel = new Label("");
        matchMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        // === TABLEAUX ===
        HBox requestHeader = new HBox(10);
        matchRequestSearchField = new TextField();
        matchRequestSearchField.setPromptText("Rechercher (club/statut/date)...");
        matchRequestCountLabel = new Label("0 demande(s)");
        matchRequestCountLabel.setStyle("-fx-text-fill: #7f8c8d;");
        requestHeader.getChildren().addAll(matchRequestSearchField, matchRequestCountLabel);

        matchRequestTable = new TableView<>();
        matchRequestTable.setPrefHeight(150);
        matchRequestTable.setMinHeight(100);
        setupMatchRequestTable();

        HBox matchHeader = new HBox(10);
        matchSearchField = new TextField();
        matchSearchField.setPromptText("Rechercher (adversaire/statut/date)...");
        matchCountLabel = new Label("0 match(s)");
        matchCountLabel.setStyle("-fx-text-fill: #7f8c8d;");
        lastRefreshLabel = new Label("Dernière mise à jour : -");
        lastRefreshLabel.setStyle("-fx-text-fill: #7f8c8d;");
        matchHeader.getChildren().addAll(matchSearchField, matchCountLabel, lastRefreshLabel);

        matchTable = new TableView<>();
        matchTable.setPrefHeight(300);
        matchTable.setMinHeight(200);
        setupMatchTable();

        // === ASSEMBLY ===
        matchView.getChildren().add(title);
        matchView.getChildren().add(formGrid);
        matchView.getChildren().add(matchMessageLabel);
        matchView.getChildren().add(new Label("Mes demandes de match :"));
        matchView.getChildren().add(requestHeader);
        matchView.getChildren().add(matchRequestTable);
        matchView.getChildren().add(new Label("Matchs des équipes :"));
        matchView.getChildren().add(matchHeader);
        matchView.getChildren().add(matchTable);

        // === ACTIONS ===
        btnCreate.setOnAction(e -> handleCreateMatchRequest());
        btnCompose.setOnAction(e -> handleOpenComposition());

        matchRequestSearchField.textProperty().addListener((obs, old, val) -> applyMatchRequestFilter());
        matchSearchField.textProperty().addListener((obs, old, val) -> applyMatchFilter());
        coachTeamCombo.setOnAction(e -> {
            refreshOpponentTeams();
            refreshMatchRequestList();
            refreshMatchList();
        });
    }

    private void setupMatchTable() {
        matchTable.getColumns().clear();

        TableColumn<Match, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        idCol.setMinWidth(50);

        TableColumn<Match, String> opponentCol = new TableColumn<>("Adversaire");
        opponentCol.setCellValueFactory(cellData -> {
            Match match = cellData.getValue();
            int opponentId = getOpponentTeamId(match);
            return new javafx.beans.property.SimpleStringProperty(getTeamDisplayById(opponentId));
        });
        opponentCol.setMinWidth(150);

        TableColumn<Match, String> dateCol = new TableColumn<>("Date/Heure");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        formatDateTime(cellData.getValue().getDateTime())));
        dateCol.setMinWidth(150);

        TableColumn<Match, String> lieuCol = new TableColumn<>("Lieu");
        lieuCol.setCellValueFactory(cellData -> {
            Match match = cellData.getValue();
            String lieu = isCoachHome(match) ? "🏠 Domicile" : "✈️ Extérieur";
            return new javafx.beans.property.SimpleStringProperty(lieu);
        });
        lieuCol.setMinWidth(100);

        TableColumn<Match, String> locCol = new TableColumn<>("Localisation");
        locCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getLocation() != null ? cellData.getValue().getLocation() : ""));
        locCol.setMinWidth(150);

        TableColumn<Match, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().name()));
        statusCol.setMinWidth(100);

        matchTable.getColumns().addAll(idCol, opponentCol, dateCol, lieuCol, locCol, statusCol);
        matchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        matchTable.setPlaceholder(new Label("Aucun match à afficher"));

        matchTable.setRowFactory(tv -> {
            TableRow<Match> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    matchTable.getSelectionModel().select(row.getItem());
                    if (event.getClickCount() == 2) {
                        openCompositionDialog(row.getItem());
                    }
                }
            });
            return row;
        });
    }

    private void setupMatchRequestTable() {
        matchRequestTable.getColumns().clear();

        TableColumn<MatchRequest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));

        TableColumn<MatchRequest, String> opponentCol = new TableColumn<>("Adversaire");
        opponentCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        getTeamDisplayById(getOpponentTeamId(cellData.getValue()))));

        TableColumn<MatchRequest, String> dateCol = new TableColumn<>("Date/Heure");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        formatDateTime(cellData.getValue().getRequestedDateTime())));

        TableColumn<MatchRequest, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().name()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                String color = switch (item) {
                    case "APPROVED" -> "#27ae60";
                    case "REJECTED" -> "#e74c3c";
                    default -> "#f39c12";
                };
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });

        matchRequestTable.getColumns().addAll(idCol, opponentCol, dateCol, statusCol);
        matchRequestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        matchRequestTable.setPlaceholder(new Label("Aucune demande"));
    }

    private void handleCreateMatchRequest() {
        if (coachClub == null) {
            showError("Erreur", "Club du coach introuvable.");
            return;
        }
        Team coachTeam = coachTeamCombo.getValue();
        if (coachTeam == null) {
            matchMessageLabel.setText("Veuillez choisir votre équipe.");
            return;
        }
        Team opponentTeam = opponentTeamCombo.getValue();
        if (opponentTeam == null) {
            matchMessageLabel.setText("Veuillez choisir une équipe adverse.");
            return;
        }
        if (coachTeam.getId() == opponentTeam.getId()) {
            matchMessageLabel.setText("Les équipes doivent être différentes.");
            return;
        }
        Integer sportId = coachTeam.getTypeSportId();
        if (sportId == null || sportId <= 0) {
            matchMessageLabel.setText("Type sport de l'équipe non défini.");
            return;
        }
        Integer opponentSportId = opponentTeam.getTypeSportId();
        if (opponentSportId != null && !opponentSportId.equals(sportId)) {
            matchMessageLabel.setText("Les équipes doivent partager le même sport.");
            return;
        }
        LocalDate date = matchDatePicker.getValue();
        LocalTime time = parseTime(matchTimeField.getText());
        if (date == null || time == null) {
            matchMessageLabel.setText("Date/heure invalide (HH:mm).");
            return;
        }
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        boolean home = "Domicile".equals(venueCombo.getValue());
        int homeId = home ? coachTeam.getId() : opponentTeam.getId();
        int awayId = home ? opponentTeam.getId() : coachTeam.getId();
        int requesterClubId = coachTeam.getClubId();
        int opponentClubId = opponentTeam.getClubId();

        MatchRequest request = new MatchRequest(
                null,
                requesterClubId,
                opponentClubId,
                homeId,
                awayId,
                sportId,
                dateTime,
                matchLocationField.getText(),
                matchRefereeField.getText(),
                currentCoach.getId(),
                MatchRequestStatus.PENDING,
                null,
                null
        );

        MatchRequest created = matchRequestController.handleCreateRequest(request);
        if (created == null) {
            String details = matchRequestController.getLastError();
            matchMessageLabel.setText(details == null ? "Erreur lors de la demande." : details);
            showError("Demande impossible", details == null ? "Vérifiez les informations du match." : details);
            return;
        }
        matchMessageLabel.setText("Demande envoyée avec succès.");
        refreshMatchRequestList();
        clearMatchFields();
    }

    private void handleOpenComposition() {
        Match selected = matchTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection requise", "Veuillez sélectionner un match.");
            return;
        }
        openCompositionDialog(selected);
    }

    private void openCompositionDialog(Match match) {
        TypeSport sport = sportController.handleGetTypeSportById(match.getTypeSportId());
        if (sport == null || sport.getRoles() == null || sport.getRoles().isEmpty()) {
            showError("Rôles manquants", "Le sport ne possède pas de rôles configurés.");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Composition - Match " + match.getId());

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        int teamId = getCoachTeamIdForMatch(match);
        if (teamId <= 0) {
            showError("Sélection invalide", "Impossible d'identifier l'équipe du coach pour ce match.");
            return;
        }
        List<User> teamPlayers = teamController.handleGetPlayers(teamId);
        List<User> eligiblePlayers = new ArrayList<>();
        if (teamPlayers != null) {
            int sportId = match.getTypeSportId();
            for (User player : teamPlayers) {
                if (player == null) {
                    continue;
                }
                if (PostgresUserDAO.getInstance().hasActiveLicenceForSport(player.getId(), sportId)) {
                    eligiblePlayers.add(player);
                }
            }
        }
        if (eligiblePlayers.isEmpty()) {
            showError("Aucun joueur", "Aucun joueur de l'équipe n'a de licence active pour ce sport.");
            return;
        }

        Label info = new Label("Saisir les joueurs pour chaque rôle :");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        List<RoleInput> inputs = new ArrayList<>();
        List<String> roles = sport.getRoles();
        int requiredCount = sport.getNbJoueurs();
        List<String> roleSlots = new ArrayList<>();
        if (roles == null || roles.size() != requiredCount) {
            for (int i = 1; i <= requiredCount; i++) {
                roleSlots.add("Poste");
            }
        } else {
            roleSlots.addAll(roles);
        }
        Map<String, Integer> counters = new HashMap<>();
        int row = 0;
        for (String role : roleSlots) {
            int slot = counters.merge(role, 1, Integer::sum);
            Label roleLabel = new Label(role + " #" + slot);
            ComboBox<User> playerCombo = new ComboBox<>();
            playerCombo.setItems(FXCollections.observableArrayList(eligiblePlayers));
            playerCombo.setCellFactory(list -> new ListCell<>() {
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
            playerCombo.setButtonCell(new ListCell<>() {
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
            grid.add(roleLabel, 0, row);
            grid.add(playerCombo, 1, row);
            inputs.add(new RoleInput(role, slot, playerCombo));
            row++;
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);

        Label message = new Label();
        message.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        Button btnSave = new Button("Enregistrer");
        btnSave.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(e -> {
            List<RoleAssignment> assignments = new ArrayList<>();
            Set<String> selectedIds = new HashSet<>();
            for (RoleInput input : inputs) {
                User selected = input.playerCombo.getValue();
                if (selected == null) {
                    message.setText("Tous les joueurs doivent être renseignés.");
                    return;
                }
                String playerId = selected.getId();
                if (!selectedIds.add(playerId)) {
                    message.setText("Un joueur ne peut occuper qu'un poste.");
                    return;
                }
                assignments.add(new RoleAssignment(input.role, input.slotIndex, playerId));
            }
            Composition composition = new Composition(match.getId(), teamId, assignments);
            boolean ok = compositionController.handleSaveComposition(composition);
            if (!ok) {
                message.setText("Composition invalide ou deadline dépassée.");
                return;
            }
            dialog.close();
        });

        root.getChildren().addAll(info, scroll, message, btnSave);

        Scene scene = new Scene(root, 420, 420);
        dialog.setScene(scene);
        dialog.show();
    }

    private void refreshMatchChoices() {
        try {
            clubCache = clubController.getAllClubs();
        } catch (SQLException e) {
            clubCache = new ArrayList<>();
        }

        List<Team> coachTeams = getCoachTeams();
        coachTeamCombo.setItems(javafx.collections.FXCollections.observableArrayList(coachTeams));
        if (!coachTeams.isEmpty()) {
            coachTeamCombo.getSelectionModel().select(coachTeams.get(0));
        }

        matchTeamCache = new ArrayList<>();
        for (Club club : clubCache) {
            List<Team> teams = teamController.handleGetTeams(club.getClubID());
            if (teams != null) {
                matchTeamCache.addAll(teams);
            }
        }
        refreshOpponentTeams();
    }

    private void refreshMatchList() {
        Integer selectedTeamId = getSelectedCoachTeamId();
        if (selectedTeamId == null) {
            matchTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        List<Match> matches = matchController.handleGetAllMatches();

        if (matches == null) {
            matchTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        List<Match> filtered = new ArrayList<>();
        for (Match match : matches) {
            if (match == null) {
                continue;
            }
            if (match.getHomeTeamId() == selectedTeamId || match.getAwayTeamId() == selectedTeamId) {
                filtered.add(match);
            }
        }
        matchCache = filtered;
        applyMatchFilter();

        if (!matchCache.isEmpty()) {
            matchMessageLabel.setText(matchCache.size() + " match(s) chargé(s)");
            matchMessageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }

    private void refreshMatchRequestList() {
        Integer selectedTeamId = getSelectedCoachTeamId();
        if (selectedTeamId == null) {
            matchRequestTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        List<MatchRequest> requests = matchRequestController.handleGetRequestsByClub(coachClubId);

        if (requests == null) {
            matchRequestTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        List<MatchRequest> filtered = new ArrayList<>();
        for (MatchRequest request : requests) {
            if (request == null) {
                continue;
            }
            if (request.getHomeTeamId() == selectedTeamId || request.getAwayTeamId() == selectedTeamId) {
                filtered.add(request);
            }
        }
        matchRequestCache = filtered;
        applyMatchRequestFilter();
    }

    private void startMatchAutoRefresh() {
        refreshMatchChoices();
        refreshMatchRequestList();
        refreshMatchList();
        updateLastRefreshLabel();

        if (matchRefreshTimeline == null) {
            matchRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                refreshMatchRequestList();
                refreshMatchList();
                updateLastRefreshLabel();
            }));
            matchRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        }
        if (matchRefreshTimeline.getStatus() != Animation.Status.RUNNING) {
            matchRefreshTimeline.play();
        }
    }

    private void clearMatchFields() {
        coachTeamCombo.getSelectionModel().clearSelection();
        opponentTeamCombo.getSelectionModel().clearSelection();
        venueCombo.getSelectionModel().selectFirst();
        matchDatePicker.setValue(null);
        matchTimeField.clear();
        matchLocationField.clear();
        matchRefereeField.clear();
    }

    // ==========================================
    // EVENT MANAGEMENT
    // ==========================================
    private void createEventView() {
        eventView = new VBox(15);
        eventView.setPadding(new Insets(20));

        Label title = new Label("Event Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        eventNameField = new TextField();
        eventNameField.setPromptText("Nom");
        eventDescriptionField = new TextArea();
        eventDescriptionField.setPromptText("Description");
        eventDescriptionField.setPrefRowCount(3);
        eventDatePicker = new DatePicker();
        eventTimeField = new TextField();
        eventTimeField.setPromptText("Heure (HH:mm)");
        eventDurationField = new TextField();
        eventDurationField.setPromptText("Durée (minutes)");
        eventLocationField = new TextField();
        eventLocationField.setPromptText("Lieu");
        eventTypeField = new TextField();
        eventTypeField.setPromptText("Type");

        Button createBtn = new Button("Créer");
        createBtn.setOnAction(e -> handleCreateEvent());

        eventMessageLabel = new Label();
        eventMessageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        eventList = new ListView<>();
        eventList.setPrefHeight(220);
        eventList.setMinHeight(180);
        eventList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " | " + formatDateTime(item.getDateDebut()));
                }
            }
        });

        eventRsvpChoice = new ChoiceBox<>(FXCollections.observableArrayList("GOING", "MAYBE", "NOT_GOING"));
        eventRsvpChoice.getSelectionModel().selectFirst();
        Button rsvpBtn = new Button("RSVP");
        rsvpBtn.setOnAction(e -> handleRsvpToEvent());

        HBox rsvpBox = new HBox(10, eventRsvpChoice, rsvpBtn);

        eventView.getChildren().addAll(
                title,
                new Label("Nouvel événement"),
                eventNameField,
                eventDescriptionField,
                new HBox(10, eventDatePicker, eventTimeField),
                new HBox(10, eventDurationField, eventLocationField),
                eventTypeField,
                createBtn,
                eventMessageLabel,
                new Separator(),
                new Label("Événements du club"),
                eventList,
                rsvpBox
        );
    }

    private void handleCreateEvent() {
        if (coachClubId <= 0) {
            showError("Erreur", "Aucun club associé.");
            return;
        }
        LocalDate date = eventDatePicker.getValue();
        LocalTime time = parseTime(eventTimeField.getText());
        if (date == null || time == null) {
            eventMessageLabel.setText("Date/heure invalides.");
            return;
        }
        LocalDateTime start = LocalDateTime.of(date, time);
        int duration;
        try {
            duration = Integer.parseInt(eventDurationField.getText().trim());
        } catch (Exception e) {
            eventMessageLabel.setText("Durée invalide.");
            return;
        }
        boolean ok = eventController.createEvent(
                eventNameField.getText(),
                eventDescriptionField.getText(),
                start,
                duration,
                eventLocationField.getText(),
                eventTypeField.getText(),
                coachClubId,
                currentCoach.getId()
        );
        eventMessageLabel.setText(ok ? "Événement créé." : eventController.getLastError());
        if (ok) {
            refreshEventList();
        }
    }

    private void handleRsvpToEvent() {
        Event selected = eventList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            eventMessageLabel.setText("Sélectionnez un événement.");
            return;
        }
        String status = eventRsvpChoice.getValue();
        boolean ok = eventController.rsvpToEvent(selected.getId(), currentCoach.getId(), status);
        eventMessageLabel.setText(ok ? "RSVP enregistré." : eventController.getLastError());
    }

    private void refreshEventList() {
        List<Event> events = eventController.getEventsByClub(coachClubId);
        List<Event> safeList = events == null ? List.of() : events;
        eventList.getSelectionModel().clearSelection();
        eventList.setItems(FXCollections.observableArrayList(safeList));
        eventList.refresh();
    }

    // ==========================================
    // COMMUNICATION
    // ==========================================
    private void createCommunicationView() {
        communicationView = new VBox(12);
        communicationView.setPadding(new Insets(20));

        Label title = new Label("Communication Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button openChat = new Button("Ouvrir le chat");
        openChat.setOnAction(e -> new CommunicationFrame(new Stage()));

        communicationView.getChildren().addAll(title, openChat);
    }

    // ==========================================
    // EQUIPMENT MANAGEMENT
    // ==========================================
    private void createEquipmentView() {
        equipmentView = new VBox(15);
        equipmentView.setPadding(new Insets(20));

        Label title = new Label("Equipement Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        equipmentMessageLabel = new Label();
        equipmentMessageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        equipmentList = new ListView<>();
        equipmentList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " | " + item.getType() + " | qty=" + item.getQuantity());
                }
            }
        });

        equipmentStartDate = new DatePicker();
        equipmentEndDate = new DatePicker();
        Button reserveBtn = new Button("Réserver");
        reserveBtn.setOnAction(e -> handleReserveEquipment());

        equipmentReservationList = new ListView<>();
        equipmentReservationList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Res#" + item.getId() + " | eq=" + item.getEquipmentId()
                            + " | " + item.getUserId() + " | " + item.getStartDate()
                            + " -> " + item.getEndDate() + " | " + item.getStatus());
                }
            }
        });
        equipmentView.getChildren().addAll(
                title,
                equipmentMessageLabel,
                new Separator(),
                new Label("Liste des équipements"),
                equipmentList,
                new HBox(10, equipmentStartDate, equipmentEndDate, reserveBtn),
                new Separator(),
                new Label("Mes réservations"),
                equipmentReservationList
        );
    }

    private void handleReserveEquipment() {
        Equipment selected = equipmentList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            equipmentMessageLabel.setText("Sélectionnez un équipement.");
            return;
        }
        LocalDate start = equipmentStartDate.getValue();
        LocalDate end = equipmentEndDate.getValue();
        boolean ok = equipmentController.handleReserveEquipment(selected.getId(), currentCoach.getId(), start, end);
        equipmentMessageLabel.setText(ok ? "Réservation envoyée." : equipmentController.getLastError());
        if (ok) {
            refreshEquipmentReservations();
        }
    }

    private void refreshEquipmentList() {
        List<Equipment> list = equipmentController.handleViewAllEquipment();
        if (list != null && coachClubId > 0) {
            list = list.stream().filter(e -> e != null && e.getClubId() == coachClubId).toList();
        }
        equipmentList.setItems(FXCollections.observableArrayList(list == null ? List.of() : list));
        refreshEquipmentReservations();
    }

    private void refreshEquipmentReservations() {
        List<Reservation> reservations = equipmentController.handleReservationsByUser(currentCoach.getId());
        equipmentReservationList.setItems(FXCollections.observableArrayList(reservations == null ? List.of() : reservations));
    }

    // ==========================================
    // TYPE EQUIPMENT MANAGEMENT
    // ==========================================
    private void createTypeEquipmentView() {
        typeEquipmentView = new VBox(15);
        typeEquipmentView.setPadding(new Insets(20));

        Label title = new Label("Type Equipement Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        equipmentTypeNameField = new TextField();
        equipmentTypeNameField.setPromptText("Nom");
        equipmentTypeDescField = new TextArea();
        equipmentTypeDescField.setPromptText("Description");
        equipmentTypeDescField.setPrefRowCount(3);

        Button createBtn = new Button("Créer");
        createBtn.setOnAction(e -> handleCreateEquipmentType());

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setOnAction(e -> handleDeleteEquipmentType());

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

        typeEquipmentView.getChildren().addAll(
                title,
                new Label("Nouveau type"),
                equipmentTypeNameField,
                equipmentTypeDescField,
                new HBox(10, createBtn, deleteBtn),
                equipmentTypeMessageLabel,
                new Separator(),
                new Label("Types existants"),
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

    private void setDisplay(javafx.scene.Node content) {
        displayArea.getChildren().clear();
        displayArea.getChildren().add(content);
    }

    private String getTeamDisplayById(int teamId) {
        if (teamId <= 0) {
            return String.valueOf(teamId);
        }
        for (Team team : matchTeamCache) {
            if (team.getId() == teamId) {
                return team.getNom() + " (" + getClubName(team.getClubId()) + ")";
            }
        }
        return String.valueOf(teamId);
    }

    private String getClubName(int clubId) {
        for (Club club : clubCache) {
            if (club.getClubID() == clubId) {
                return club.getName();
            }
        }
        return String.valueOf(clubId);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void applyMatchRequestFilter() {
        if (matchRequestSearchField == null) return;
        String q = matchRequestSearchField.getText() == null ? "" : matchRequestSearchField.getText().trim().toLowerCase();
        java.util.List<MatchRequest> filtered = new java.util.ArrayList<>();
        for (MatchRequest r : matchRequestCache) {
            String opponent = getTeamDisplayById(getOpponentTeamId(r)).toLowerCase();
            String status = r.getStatus().name().toLowerCase();
            String date = formatDateTime(r.getRequestedDateTime()).toLowerCase();
            if (q.isEmpty() || opponent.contains(q) || status.contains(q) || date.contains(q)) {
                filtered.add(r);
            }
        }
        matchRequestTable.setItems(javafx.collections.FXCollections.observableArrayList(filtered));
        if (matchRequestCountLabel != null) {
            matchRequestCountLabel.setText(filtered.size() + " demande(s)");
        }
    }

    private void applyMatchFilter() {
        if (matchSearchField == null) return;
        String q = matchSearchField.getText() == null ? "" : matchSearchField.getText().trim().toLowerCase();
        java.util.List<Match> filtered = new java.util.ArrayList<>();
        for (Match m : matchCache) {
            int opponentId = getOpponentTeamId(m);
            String opponent = getTeamDisplayById(opponentId).toLowerCase();
            String status = m.getStatus().name().toLowerCase();
            String date = formatDateTime(m.getDateTime()).toLowerCase();
            if (q.isEmpty() || opponent.contains(q) || status.contains(q) || date.contains(q)) {
                filtered.add(m);
            }
        }
        matchTable.setItems(javafx.collections.FXCollections.observableArrayList(filtered));
        if (matchCountLabel != null) {
            matchCountLabel.setText(filtered.size() + " match(s)");
        }
    }

    private void updateLastRefreshLabel() {
        if (lastRefreshLabel != null) {
            lastRefreshLabel.setText("Dernière mise à jour : " + formatDateTime(LocalDateTime.now()));
        }
    }

    private void createLicenceView() {
        licenceView = new VBox(20);
        licenceView.setPadding(new Insets(30));

        Label title = new Label("Ma Licence Sportive");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        licenceStatusBox = new VBox(10);
        licenceStatusBox.setPadding(new Insets(15));
        licenceStatusBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        Label formTitle = new Label("Nouvelle demande de licence");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        ComboBox<TypeSport> sportCombo = new ComboBox<>();
        sportCombo.setPromptText("Sélectionnez un sport");
        sportCombo.setMaxWidth(Double.MAX_VALUE);

        try {
            List<TypeSport> sports = TypeSportFacade.getInstance().getAllTypeSports();
            sportCombo.setItems(FXCollections.observableArrayList(sports));
        } catch (Exception e) {
            System.err.println("Erreur chargement sports : " + e.getMessage());
        }

        ComboBox<TypeLicence> typeCombo = new ComboBox<>(FXCollections.observableArrayList(TypeLicence.values()));
        typeCombo.setPromptText("Type (COACH...)");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        typeCombo.getSelectionModel().select(TypeLicence.COACH);

        Button btnUpload = new Button("Joindre Certificat Médical");
        styleButton(btnUpload, "#95a5a6");
        btnUpload.setOnAction(e -> {
            documentAttached = true;
            btnUpload.setText("Certificat Médical Joint");
            btnUpload.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        });

        Button submitBtn = new Button("Envoyer la demande");
        styleButton(submitBtn, "#2ecc71");

        submitBtn.setOnAction(e -> {
            TypeSport selectedSport = sportCombo.getValue();
            TypeLicence selectedType = typeCombo.getValue();

            if (selectedSport == null || selectedType == null || !documentAttached) {
                showError("Documents manquants", "Veuillez remplir tous les champs et joindre votre certificat.");
                return;
            }

            try {
                licenceController.onDemandeLicence(selectedSport, selectedType);
                documentAttached = false;
                btnUpload.setText("Joindre Certificat Médical");
                btnUpload.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
                refreshLicenceInfo();
                showInfo("Succès", "Votre demande a été enregistrée (Statut: En attente).");
            } catch (Exception ex) {
                showError("Demande refusée", ex.getMessage());
            }
        });

        formBox.getChildren().addAll(formTitle, new Label("Discipline :"), sportCombo, new Label("Type :"), typeCombo, btnUpload, submitBtn);

        ScrollPane scrollPane = new ScrollPane(licenceStatusBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(320);
        scrollPane.setMinHeight(280);

        licenceView.getChildren().addAll(title, new Label("Historique de vos licences :"), scrollPane, new Separator(), formBox);
    }

    private void refreshLicenceInfo() {
        licenceStatusBox.getChildren().clear();
        List<Licence> licences = LicenceFacade.getInstance().getLicencesByMembre(currentCoach.getId());
        if (licences == null || licences.isEmpty()) {
            Label empty = new Label("Aucune licence enregistrée.");
            empty.setStyle("-fx-text-fill: #7f8c8d;");
            licenceStatusBox.getChildren().add(empty);
            return;
        }

        for (Licence l : licences) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(8));
            row.setStyle("-fx-background-color: #f7f7f7; -fx-background-radius: 6;");

            String sportName = l.getSport() == null ? "" : l.getSport().getNom();
            Label sportLabel = new Label(sportName + " (" + l.getTypeLicence() + ")");
            Label statusLabel = new Label(l.getStatut().name());
            String color = "#7f8c8d";
            if (l.getStatut() == StatutLicence.ACTIVE) color = "#27ae60";
            if (l.getStatut() == StatutLicence.REFUSEE) color = "#c0392b";
            statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            row.getChildren().addAll(sportLabel, spacer, statusLabel);
            licenceStatusBox.getChildren().add(row);
        }
    }

    private Integer getSelectedCoachTeamId() {
        Team selected = coachTeamCombo != null ? coachTeamCombo.getValue() : null;
        return selected != null ? selected.getId() : null;
    }

    private List<Integer> getCoachTeamIds() {
        List<Team> teams = getCoachTeams();
        List<Integer> ids = new ArrayList<>();
        for (Team team : teams) {
            if (team != null) {
                ids.add(team.getId());
            }
        }
        return ids;
    }

    private int getOpponentTeamId(Match match) {
        if (match == null) {
            return -1;
        }
        Integer selectedTeamId = getSelectedCoachTeamId();
        if (selectedTeamId != null && match.getHomeTeamId() == selectedTeamId) {
            return match.getAwayTeamId();
        }
        if (selectedTeamId != null && match.getAwayTeamId() == selectedTeamId) {
            return match.getHomeTeamId();
        }
        return -1;
    }

    private boolean isCoachHome(Match match) {
        Integer selectedTeamId = getSelectedCoachTeamId();
        return match != null && selectedTeamId != null && match.getHomeTeamId() == selectedTeamId;
    }

    private int getOpponentTeamId(MatchRequest request) {
        if (request == null) {
            return -1;
        }
        Integer selectedTeamId = getSelectedCoachTeamId();
        if (selectedTeamId != null && request.getHomeTeamId() == selectedTeamId) {
            return request.getAwayTeamId();
        }
        if (selectedTeamId != null && request.getAwayTeamId() == selectedTeamId) {
            return request.getHomeTeamId();
        }
        return -1;
    }

    private int getCoachTeamIdForMatch(Match match) {
        if (match == null) {
            return -1;
        }
        Integer selectedTeamId = getSelectedCoachTeamId();
        if (selectedTeamId != null && match.getHomeTeamId() == selectedTeamId) {
            return match.getHomeTeamId();
        }
        if (selectedTeamId != null && match.getAwayTeamId() == selectedTeamId) {
            return match.getAwayTeamId();
        }
        return -1;
    }

    private void refreshOpponentTeams() {
        Team coachTeam = coachTeamCombo.getValue();
        if (coachTeam == null) {
            opponentTeamCombo.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }
        Integer sportId = coachTeam.getTypeSportId();
        List<Team> opponents = new ArrayList<>();
        for (Team team : matchTeamCache) {
            if (team == null || team.getId() == coachTeam.getId()) {
                continue;
            }
            if (sportId != null && sportId > 0) {
                Integer teamSportId = team.getTypeSportId();
                if (teamSportId == null || !sportId.equals(teamSportId)) {
                    continue;
                }
            }
            opponents.add(team);
        }
        opponentTeamCombo.setItems(javafx.collections.FXCollections.observableArrayList(opponents));
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalTime.parse(value.trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            return null;
        }
    }

    private void loadCoachClub() {
        if (coachClubId == -1) {
            coachClub = null;
            return;
        }
        try {
            coachClub = clubController.getClubById(coachClubId);
        } catch (SQLException e) {
            coachClub = null;
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

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private VBox createCoachPlaceholder(String titleText, String bodyText) {
        VBox view = new VBox(12);
        view.setPadding(new Insets(30));

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label body = new Label(bodyText);
        body.setStyle("-fx-text-fill: #7f8c8d;");

        view.getChildren().addAll(title, body);
        return view;
    }

    private void handleLogout(Stage currentStage) {
        if (matchRefreshTimeline != null) {
            matchRefreshTimeline.stop();
        }
        currentStage.close();
        new LoginFrame().start(new Stage());
    }

    private static class RoleInput {
        private final String role;
        private final int slotIndex;
        private final ComboBox<User> playerCombo;

        private RoleInput(String role, int slotIndex, ComboBox<User> playerCombo) {
            this.role = role;
            this.slotIndex = slotIndex;
            this.playerCombo = playerCombo;
        }
    }
}
