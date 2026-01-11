package com.sportify.manager.frame;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.CompositionController;
import com.sportify.manager.controllers.MatchController;
import com.sportify.manager.controllers.MatchRequestController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.controllers.TrainingController;
import com.sportify.manager.controllers.EventController;
import com.sportify.manager.controllers.EquipmentController;
import com.sportify.manager.controllers.EquipmentTypeController;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.Composition;
import com.sportify.manager.services.Equipment;
import com.sportify.manager.services.EquipmentType;
import com.sportify.manager.services.EquipmentTypeActionResult;
import com.sportify.manager.services.Event;
import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.MatchRequestStatus;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.Reservation;
import com.sportify.manager.services.RoleAssignment;
import com.sportify.manager.services.Training;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.User;
import com.sportify.manager.frame.CommunicationFrame;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoachDashboardFrame extends Application {
    private User currentCoach;
    private int coachClubId;
    private Club coachClub;

    private ClubController clubController;
    private MatchController matchController;
    private MatchRequestController matchRequestController;
    private CompositionController compositionController;
    private TypeSportController sportController;
    private TrainingController trainingController;
    private EventController eventController = EventController.getInstance();
    private EquipmentController equipmentController = new EquipmentController();
    private EquipmentTypeController equipmentTypeController = new EquipmentTypeController();

    private StackPane displayArea;

    // Match View
    private VBox matchView;
    private TableView<Match> matchTable;
    private TableView<MatchRequest> matchRequestTable;
    private TextField matchRequestSearchField;
    private TextField matchSearchField;
    private Label matchRequestCountLabel;
    private Label matchCountLabel;
    private Label lastRefreshLabel;
    private java.util.List<MatchRequest> matchRequestCache = new java.util.ArrayList<>();
    private java.util.List<Match> matchCache = new java.util.ArrayList<>();
    private ComboBox<Club> opponentClubCombo;
    private ComboBox<String> venueCombo;
    private DatePicker matchDatePicker;
    private TextField matchTimeField;
    private TextField matchLocationField;
    private TextField matchRefereeField;
    private Label matchMessageLabel;
    private List<Club> clubCache = new ArrayList<>();
    private Timeline matchRefreshTimeline;

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
    private TableView<Training> trainingTable;
    private DatePicker trainingFromDatePicker;
    private TextField participationUserIdField;
    private ChoiceBox<ParticipationStatus> statusChoiceBox;
    private ListView<String> participationList;
    private Label trainingMessageLabel;

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
    private TextField equipmentNameField;
    private TextField equipmentTypeField;
    private TextField equipmentConditionField;
    private TextField equipmentQuantityField;
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

        Button btnTeam = createMenuButton("👥 Team Management");
        Button btnTraining = createMenuButton("🏋️ Training Management");
        Button btnMatch = createMenuButton("⚽ Match Management");
        Button btnStats = createMenuButton("📊 Stat Management");
        Button btnEvents = createMenuButton("📅 Event Management");
        Button btnCommunication = createMenuButton("💬 Communication Management");
        Button btnEquipment = createMenuButton("🧰 Equipement Management");
        Button btnLogout = createMenuButton("🚪 Déconnexion");

        sidebar.getChildren().addAll(
                menuLabel,
                new Separator(),
                btnTeam,
                btnTraining,
                btnMatch,
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
        createTrainingView();
        createEventView();
        createCommunicationView();
        createEquipmentView();
        createTypeEquipmentView();

        // --- ACTIONS ---
        btnStats.setOnAction(e -> {
            if (coachClubId != -1) {
                StatFrame statFrame = new StatFrame();
                statFrame.show(coachClubId);
            } else {
                placeholderText.setText("Erreur : Aucun club n'est associé à votre compte coach.");
            }
        });

        btnTeam.setOnAction(e -> setDisplay(createCoachPlaceholder("Team Management", "Ce module sera ajouté prochainement.")));
        btnTraining.setOnAction(e -> {
            if (coachClubId == -1) {
                showError("Erreur", "Aucun club n'est associé à votre compte coach.");
                return;
            }
            setDisplay(trainingView);
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
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

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

        Button createButton = new Button("➕ Planifier");
        createButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        createButton.setOnAction(e -> handleCreateTraining());

        HBox createRow = new HBox(10, createButton);
        createRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // === LISTE DES ENTRAÎNEMENTS ===
        Label upcomingLabel = new Label("Entraînements à venir");
        upcomingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        filterRow.getChildren().add(new Label("À partir du :"));
        trainingFromDatePicker = new DatePicker(LocalDate.now());
        Button refreshButton = new Button("🔄 Rafraîchir");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> refreshTrainingList());
        filterRow.getChildren().addAll(trainingFromDatePicker, refreshButton);

        trainingTable = new TableView<>();
        trainingTable.setPrefHeight(220);
        setupTrainingTable();

        trainingTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> loadTrainingParticipation()
        );

        // === PARTICIPATION ===
        Label participationLabel = new Label("Participation");
        participationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        participationUserIdField = new TextField();
        participationUserIdField.setPromptText("User ID (ex: " + currentCoach.getId() + ")");

        statusChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(ParticipationStatus.values()));
        statusChoiceBox.getSelectionModel().select(ParticipationStatus.PENDING);

        Button markButton = new Button("✓ Marquer");
        markButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        markButton.setOnAction(e -> handleMarkParticipation());

        HBox participationRow = new HBox(10, participationUserIdField, statusChoiceBox, markButton);
        participationRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        participationList = new ListView<>();
        participationList.setPrefHeight(120);

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
                new Separator(),
                participationLabel,
                participationRow,
                participationList,
                trainingMessageLabel
        );
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

        trainingTable.getColumns().addAll(idCol, dateCol, timeCol, locationCol, activityCol);
        trainingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trainingTable.setPlaceholder(new Label("Aucun entraînement à afficher"));
    }

    private void handleCreateTraining() {
        trainingController.onCreate();
    }

    private void refreshTrainingList() {
        trainingController.onRefresh();
    }

    private void loadTrainingParticipation() {
        trainingController.onLoadParticipation();
    }

    private void handleMarkParticipation() {
        trainingController.onMarkParticipation();
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

    public LocalDate getFromDate() {
        return trainingFromDatePicker != null ? trainingFromDatePicker.getValue() : null;
    }

    public int getSelectedTrainingId() {
        if (trainingTable == null || trainingTable.getSelectionModel().getSelectedItem() == null) {
            return -1;
        }
        Training selected = trainingTable.getSelectionModel().getSelectedItem();
        return selected.getId();
    }

    public String getParticipationUserId() {
        return participationUserIdField != null ? participationUserIdField.getText() : "";
    }

    public ParticipationStatus getSelectedStatus() {
        ParticipationStatus status = statusChoiceBox != null ? statusChoiceBox.getValue() : null;
        return status != null ? status : ParticipationStatus.PENDING;
    }

    public void setTrainings(List<Training> trainings) {
        if (trainingTable == null) {
            return;
        }
        trainingTable.setItems(FXCollections.observableArrayList(trainings));
    }

    public void setParticipation(Map<User, ParticipationStatus> participation) {
        if (participationList == null) {
            return;
        }
        List<String> rows = new ArrayList<>();
        for (Map.Entry<User, ParticipationStatus> entry : participation.entrySet()) {
            User user = entry.getKey();
            ParticipationStatus status = entry.getValue();
            rows.add(user.getId() + " - " + user.getName() + " (" + status + ")");
        }
        participationList.setItems(FXCollections.observableArrayList(rows));
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

    public void clearParticipation() {
        clearParticipationList();
    }

    public void clearTrainingForm() {
        if (trainingDatePicker != null) trainingDatePicker.setValue(null);
        if (trainingTimeField != null) trainingTimeField.clear();
        if (trainingLocationField != null) trainingLocationField.clear();
        if (trainingActivityField != null) trainingActivityField.clear();
    }

    private void clearParticipationList() {
        if (participationList != null) {
            participationList.getItems().clear();
        }
    }

    // ==========================================
    // MATCHS COACH
    // ==========================================
    private void createMatchView() {
        matchView = new VBox(20);
        matchView.setPadding(new Insets(20));

        Label title = new Label("Matchs du Club");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // === FORMULAIRE ===
        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(15));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dcdde1;");

        opponentClubCombo = new ComboBox<>();
        opponentClubCombo.setPromptText("Club adverse");

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

        formGrid.add(new Label("Adversaire :"), 0, 0);
        formGrid.add(opponentClubCombo, 1, 0);
        formGrid.add(new Label("Lieu :"), 2, 0);
        formGrid.add(venueCombo, 3, 0);

        formGrid.add(new Label("Date :"), 0, 1);
        formGrid.add(matchDatePicker, 1, 1);
        formGrid.add(new Label("Heure :"), 2, 1);
        formGrid.add(matchTimeField, 3, 1);

        formGrid.add(new Label("Localisation :"), 0, 2);
        formGrid.add(matchLocationField, 1, 2);
        formGrid.add(new Label("Arbitre :"), 2, 2);
        formGrid.add(matchRefereeField, 3, 2);

        Button btnCreate = new Button("➕ Demander");
        btnCreate.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnCompose = new Button("📋 Composer équipe");
        btnCompose.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox actions = new HBox(10, btnCreate, btnCompose);
        formGrid.add(actions, 1, 3, 3, 1);

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
        matchView.getChildren().add(new Label("Matchs du club :"));
        matchView.getChildren().add(matchHeader);
        matchView.getChildren().add(matchTable);

        // === ACTIONS ===
        btnCreate.setOnAction(e -> handleCreateMatchRequest());
        btnCompose.setOnAction(e -> handleOpenComposition());

        matchRequestSearchField.textProperty().addListener((obs, old, val) -> applyMatchRequestFilter());
        matchSearchField.textProperty().addListener((obs, old, val) -> applyMatchFilter());
    }

    private void setupMatchTable() {
        matchTable.getColumns().clear();

        TableColumn<Match, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        idCol.setMinWidth(50);

        TableColumn<Match, String> opponentCol = new TableColumn<>("Adversaire");
        opponentCol.setCellValueFactory(cellData -> {
            Match match = cellData.getValue();
            int opponentId = (match.getHomeTeamId() == coachClubId)
                    ? match.getAwayTeamId()
                    : match.getHomeTeamId();
            return new javafx.beans.property.SimpleStringProperty(getOpponentName(opponentId));
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
            String lieu = (match.getHomeTeamId() == coachClubId) ? "🏠 Domicile" : "✈️ Extérieur";
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
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openCompositionDialog(row.getItem());
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
                        getOpponentName(cellData.getValue().getOpponentClubId())));

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
        if (coachClub.getSportId() <= 0) {
            matchMessageLabel.setText("Le club n'a pas de sport associé.");
            return;
        }
        Club opponent = opponentClubCombo.getValue();
        if (opponent == null) {
            matchMessageLabel.setText("Veuillez choisir un club adverse.");
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
        int homeId = home ? coachClub.getClubID() : opponent.getClubID();
        int awayId = home ? opponent.getClubID() : coachClub.getClubID();

        MatchRequest request = new MatchRequest(
                null,
                coachClubId,
                opponent.getClubID(),
                homeId,
                awayId,
                coachClub.getSportId(),
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

        Label info = new Label("Saisir les joueurs pour chaque rôle :");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        List<RoleInput> inputs = new ArrayList<>();
        Map<String, Integer> counters = new HashMap<>();
        int row = 0;
        for (String role : sport.getRoles()) {
            int slot = counters.merge(role, 1, Integer::sum);
            Label roleLabel = new Label(role + " #" + slot);
            TextField playerField = new TextField();
            playerField.setPromptText("playerId");
            grid.add(roleLabel, 0, row);
            grid.add(playerField, 1, row);
            inputs.add(new RoleInput(role, slot, playerField));
            row++;
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);

        Label message = new Label();
        message.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        Button btnSave = new Button("✅ Enregistrer");
        btnSave.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(e -> {
            List<RoleAssignment> assignments = new ArrayList<>();
            for (RoleInput input : inputs) {
                String playerId = input.playerField.getText().trim();
                if (playerId.isEmpty()) {
                    message.setText("Tous les joueurs doivent être renseignés.");
                    return;
                }
                assignments.add(new RoleAssignment(input.role, input.slotIndex, playerId));
            }

            Composition composition = new Composition(match.getId(), coachClubId, assignments);
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

        List<Club> opponents = new ArrayList<>();
        for (Club club : clubCache) {
            if (club.getClubID() != coachClubId) {
                opponents.add(club);
            }
        }

        opponentClubCombo.setItems(javafx.collections.FXCollections.observableArrayList(opponents));
    }

    private void refreshMatchList() {
        if (coachClubId <= 0) {
            matchTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        List<Match> matches = matchController.handleGetMatchesByClub(coachClubId);

        if (matches == null) {
            matchTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        matchCache = matches;
        applyMatchFilter();

        if (!matches.isEmpty()) {
            matchMessageLabel.setText("✅ " + matches.size() + " match(s) chargé(s)");
            matchMessageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }

    private void refreshMatchRequestList() {
        if (coachClubId <= 0) {
            matchRequestTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        List<MatchRequest> requests = matchRequestController.handleGetRequestsByClub(coachClubId);

        if (requests == null) {
            matchRequestTable.setItems(javafx.collections.FXCollections.observableArrayList());
            return;
        }

        matchRequestCache = requests;
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
        opponentClubCombo.getSelectionModel().clearSelection();
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
        eventList.setItems(FXCollections.observableArrayList(events == null ? List.of() : events));
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

        equipmentNameField = new TextField();
        equipmentNameField.setPromptText("Nom");
        equipmentTypeField = new TextField();
        equipmentTypeField.setPromptText("Type");
        equipmentConditionField = new TextField();
        equipmentConditionField.setPromptText("État");
        equipmentQuantityField = new TextField();
        equipmentQuantityField.setPromptText("Quantité");

        Button addBtn = new Button("Ajouter");
        addBtn.setOnAction(e -> handleAddEquipment());

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
        Button approveBtn = new Button("Approuver");
        approveBtn.setOnAction(e -> handleReservationStatusUpdate("APPROVED"));
        Button rejectBtn = new Button("Refuser");
        rejectBtn.setOnAction(e -> handleReservationStatusUpdate("REJECTED"));

        equipmentView.getChildren().addAll(
                title,
                new Label("Ajouter un équipement"),
                equipmentNameField,
                new HBox(10, equipmentTypeField, equipmentConditionField),
                equipmentQuantityField,
                addBtn,
                equipmentMessageLabel,
                new Separator(),
                new Label("Liste des équipements"),
                equipmentList,
                new HBox(10, equipmentStartDate, equipmentEndDate, reserveBtn),
                new Separator(),
                new Label("Réservations du club"),
                equipmentReservationList,
                new HBox(10, approveBtn, rejectBtn)
        );
    }

    private void handleAddEquipment() {
        int qty;
        try {
            qty = Integer.parseInt(equipmentQuantityField.getText().trim());
        } catch (Exception e) {
            equipmentMessageLabel.setText("Quantité invalide.");
            return;
        }
        boolean ok = equipmentController.handleCreateEquipment(
                equipmentNameField.getText(),
                equipmentTypeField.getText(),
                equipmentConditionField.getText(),
                qty,
                coachClubId
        );
        equipmentMessageLabel.setText(ok ? "Équipement ajouté." : equipmentController.getLastError());
        if (ok) {
            refreshEquipmentList();
        }
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
        if (coachClubId <= 0) {
            equipmentReservationList.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Reservation> reservations = equipmentController.handleReservationsByClub(coachClubId);
        equipmentReservationList.setItems(FXCollections.observableArrayList(reservations == null ? List.of() : reservations));
    }

    private void handleReservationStatusUpdate(String status) {
        Reservation selected = equipmentReservationList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            equipmentMessageLabel.setText("Sélectionnez une réservation.");
            return;
        }
        boolean ok = equipmentController.handleUpdateReservationStatus(selected.getId(), status);
        equipmentMessageLabel.setText(ok ? "Statut mis à jour." : equipmentController.getLastError());
        if (ok) {
            refreshEquipmentReservations();
        }
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

    private String getOpponentName(int clubId) {
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
            String opponent = getOpponentName(r.getOpponentClubId()).toLowerCase();
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
            int opponentId = m.getHomeTeamId() == coachClubId ? m.getAwayTeamId() : m.getHomeTeamId();
            String opponent = getOpponentName(opponentId).toLowerCase();
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
        private final TextField playerField;

        private RoleInput(String role, int slotIndex, TextField playerField) {
            this.role = role;
            this.slotIndex = slotIndex;
            this.playerField = playerField;
        }
    }
}
