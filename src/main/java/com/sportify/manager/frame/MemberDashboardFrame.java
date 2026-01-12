package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.LicenceController;
import com.sportify.manager.controllers.EventController;
import com.sportify.manager.controllers.TeamController;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.facade.TrainingFacade;
import com.sportify.manager.facade.TypeSportFacade;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.Event;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.Training;
import com.sportify.manager.services.User;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.frame.CommunicationFrame;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberDashboardFrame extends Application {
    private ClubController clubController;
    private LicenceController licenceController;
    private final EventController eventController = EventController.getInstance();
    private final TeamController teamController = TeamController.getInstance();
    private final TrainingFacade trainingFacade = TrainingFacade.getInstance();
    private User currentUser;
    private int memberClubId = -1;

    private TableView<Club> clubTable;
    private ChoiceBox<String> joinRoleChoice;
    private VBox clubView;
    private VBox licenceView;
    private VBox trainingView;
    private VBox eventView;
    private VBox communicationView;
    private StackPane contentArea;
    private VBox licenceStatusBox;

    private ListView<Event> eventList;
    private DatePicker eventStartDate;
    private DatePicker eventEndDate;
    private ChoiceBox<String> eventRsvpChoice;
    private Label eventMessageLabel;

    private ComboBox<Team> memberTeamCombo;
    private TableView<Training> trainingTable;
    private ChoiceBox<ParticipationStatus> trainingStatusChoice;
    private Label trainingMessageLabel;
    private final Map<Integer, String> memberTeamNames = new HashMap<>();

    private boolean documentAttached = false;

    public MemberDashboardFrame(User user) {
        this.currentUser = user;
        this.licenceController = new LicenceController();
        this.licenceController.setCurrentUser(user);
        this.memberClubId = PostgresUserDAO.getInstance().getClubIdByMember(user.getId());
    }

    public void setClubController(ClubController controller) {
        this.clubController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        if (clubController == null) clubController = new ClubController(null);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f7f6;");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("MON ESPACE");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnClubs = createMenuButton("Parcourir Clubs", true);
        Button btnLicence = createMenuButton(" Ma Licence", false);
        Button btnTraining = createMenuButton(" Trainings", false);
        Button btnStats = createMenuButton("Stat Management", false);
        Button btnEvents = createMenuButton("Events", false);
        Button btnCommunication = createMenuButton("Communication", false);
        Button btnLogout = createMenuButton("Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnLicence, btnTraining, btnStats, btnEvents, btnCommunication, btnLogout);

        createClubView();
        createLicenceView();
        createTrainingView();
        createEventView();
        createCommunicationView();

        contentArea = new StackPane(clubView, licenceView, trainingView, eventView, communicationView);
        licenceView.setVisible(false);
        trainingView.setVisible(false);
        eventView.setVisible(false);
        communicationView.setVisible(false);

        btnClubs.setOnAction(e -> {
            switchView(btnClubs, btnLicence, btnTraining, btnStats, btnEvents, btnCommunication);
            clubView.setVisible(true);
            licenceView.setVisible(false);
            trainingView.setVisible(false);
            eventView.setVisible(false);
            communicationView.setVisible(false);
            refreshList();
        });

        btnLicence.setOnAction(e -> {
            switchView(btnLicence, btnClubs, btnTraining, btnStats, btnEvents, btnCommunication);
            clubView.setVisible(false);
            licenceView.setVisible(true);
            trainingView.setVisible(false);
            eventView.setVisible(false);
            communicationView.setVisible(false);
            refreshLicenceInfo();
        });

        btnTraining.setOnAction(e -> {
            switchView(btnTraining, btnClubs, btnLicence, btnStats, btnEvents,  btnCommunication);
            clubView.setVisible(false);
            licenceView.setVisible(false);
            trainingView.setVisible(true);
            eventView.setVisible(false);
            communicationView.setVisible(false);
            refreshMemberTeams();
            refreshTrainingList();
        });

        btnStats.setOnAction(e -> {
            switchView(btnStats, btnClubs, btnLicence, btnTraining, btnEvents,  btnCommunication);
            openMemberStats();
        });

        btnEvents.setOnAction(e -> {
            switchView(btnEvents, btnClubs, btnLicence, btnTraining, btnStats, btnCommunication);
            clubView.setVisible(false);
            licenceView.setVisible(false);
            trainingView.setVisible(false);
            eventView.setVisible(true);
            communicationView.setVisible(false);
            refreshEventList();
        });

        btnCommunication.setOnAction(e -> {
            switchView(btnCommunication, btnClubs, btnLicence, btnTraining, btnStats, btnEvents);
            clubView.setVisible(false);
            licenceView.setVisible(false);
            trainingView.setVisible(false);
            eventView.setVisible(false);
            communicationView.setVisible(true);
        });

        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 950, 650);
        primaryStage.setTitle("Sportify - Espace Membre");
        primaryStage.setScene(scene);
        refreshList();
        primaryStage.show();
    }

    private void createClubView() {
        clubView = new VBox(20);
        clubView.setPadding(new Insets(30));
        Label welcomeLabel = new Label("Bonjour, " + currentUser.getName());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        clubTable = new TableView<>();
        setupClubTable();

        joinRoleChoice = new ChoiceBox<>(FXCollections.observableArrayList("JOUEUR", "COACH"));
        joinRoleChoice.getSelectionModel().selectFirst();

        Button joinButton = new Button("Demander à rejoindre");
        styleButton(joinButton, "#3498db");
        joinButton.setOnAction(e -> {
            Club selected = clubTable.getSelectionModel().getSelectedItem();
            if (selected != null) handleJoinRequest(selected);
            else showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez choisir un club.");
        });

        clubView.getChildren().addAll(welcomeLabel, new Label("Clubs disponibles :"), clubTable, joinRoleChoice, joinButton);
        VBox.setVgrow(clubTable, Priority.ALWAYS);
    }

    private void createLicenceView() {
        licenceView = new VBox(20);
        licenceView.setPadding(new Insets(30));

        Label title = new Label("Ma Licence Sportive");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        licenceStatusBox = new VBox(10);
        licenceStatusBox.setPadding(new Insets(15));
        licenceStatusBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dcdde1;");

        // --- FORMULAIRE DE DEMANDE (CRITÈRE 7.1) ---
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

        ComboBox<TypeLicence> typeCombo = new ComboBox<>(FXCollections.observableArrayList(TypeLicence.JOUEUR));
        typeCombo.setPromptText("Type (JOUEUR, COACH...)");
        typeCombo.setMaxWidth(Double.MAX_VALUE);


        Button btnUpload = new Button("Joindre Certificat Médical");
        styleButton(btnUpload, "#95a5a6");
        btnUpload.setOnAction(e -> {
            documentAttached = true;
            btnUpload.setText(" Certificat Médical Joint");
            btnUpload.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        });

        Button submitBtn = new Button("Envoyer la demande");
        styleButton(submitBtn, "#2ecc71");

        submitBtn.setOnAction(e -> {
            TypeSport selectedSport = sportCombo.getValue();
            TypeLicence selectedType = typeCombo.getValue();

            // Vérification Flux A1
            if (selectedSport == null || selectedType == null || !documentAttached) {
                showAlert(Alert.AlertType.ERROR, "Documents manquants", "Veuillez remplir tous les champs et joindre votre certificat.");
                return;
            }

            try {
                // Appel au controller (qui appellera le manager et lancera le flux A2 si besoin)
                licenceController.onDemandeLicence(selectedSport, selectedType);

                // Reset formulaire
                documentAttached = false;
                btnUpload.setText(" Joindre Certificat Médical");
                btnUpload.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

                refreshLicenceInfo();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre demande a été enregistrée (Statut: En attente).");
            } catch (Exception ex) {
                // CAPTURE DU FLUX A2 : Licence déjà existante
                showAlert(Alert.AlertType.ERROR, "Demande refusée", ex.getMessage());
            }
        });

        formBox.getChildren().addAll(formTitle, new Label("Discipline :"), sportCombo, new Label("Type :"), typeCombo, btnUpload, submitBtn);

        ScrollPane scrollPane = new ScrollPane(licenceStatusBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);

        licenceView.getChildren().addAll(title, new Label("Historique de vos licences (Critère 7.4) :"), scrollPane, new Separator(), formBox);
    }

    private void createTrainingView() {
        trainingView = new VBox(20);
        trainingView.setPadding(new Insets(30));

        Label title = new Label("Mes Entraînements");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        memberTeamCombo = new ComboBox<>();
        memberTeamCombo.setPromptText("Choisir une équipe");
        memberTeamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (" + item.getCategorie() + ")");
                }
            }
        });
        memberTeamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " (" + item.getCategorie() + ")");
                }
            }
        });
        memberTeamCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshTrainingList());

        trainingTable = new TableView<>();
        setupTrainingTable();

        trainingStatusChoice = new ChoiceBox<>(FXCollections.observableArrayList(ParticipationStatus.values()));
        trainingStatusChoice.getSelectionModel().select(ParticipationStatus.PENDING);
        Button markBtn = new Button("Marquer");
        styleButton(markBtn, "#f39c12");
        markBtn.setOnAction(e -> handleTrainingParticipation());

        HBox actions = new HBox(10, trainingStatusChoice, markBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        trainingMessageLabel = new Label();
        trainingMessageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        trainingView.getChildren().addAll(
                title,
                new Label("Mes équipes :"),
                memberTeamCombo,
                new Separator(),
                trainingTable,
                actions,
                trainingMessageLabel
        );
        VBox.setVgrow(trainingTable, Priority.ALWAYS);
    }

    private void createEventView() {
        eventView = new VBox(15);
        eventView.setPadding(new Insets(30));

        Label title = new Label("Événements");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        eventStartDate = new DatePicker();
        eventEndDate = new DatePicker();
        Button loadBtn = new Button("Charger");
        loadBtn.setOnAction(e -> refreshEventList());

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
        rsvpBtn.setOnAction(e -> handleRsvp());

        eventMessageLabel = new Label();
        eventMessageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        HBox filters = new HBox(10, eventStartDate, eventEndDate, loadBtn);
        HBox rsvpBox = new HBox(10, eventRsvpChoice, rsvpBtn);

        eventView.getChildren().addAll(title, filters, eventList, rsvpBox, eventMessageLabel);
    }

    private void setupTrainingTable() {
        TableColumn<Training, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Training, LocalTime> timeCol = new TableColumn<>("Heure");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("heure"));

        TableColumn<Training, String> teamCol = new TableColumn<>("Équipe");
        teamCol.setCellValueFactory(cell -> new SimpleStringProperty(getMemberTeamName(cell.getValue().getTeamId())));

        TableColumn<Training, String> locationCol = new TableColumn<>("Lieu");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        TableColumn<Training, String> activityCol = new TableColumn<>("Activité");
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activite"));

        trainingTable.getColumns().addAll(dateCol, timeCol, teamCol, locationCol, activityCol);
        trainingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trainingTable.setPlaceholder(new Label("Aucun entraînement à afficher"));
    }

    private void refreshMemberTeams() {
        List<Team> teams = teamController.handleGetTeamsByMember(currentUser.getId());
        memberTeamNames.clear();
        if (teams != null) {
            for (Team t : teams) {
                if (t != null) {
                    memberTeamNames.put(t.getId(), t.getNom());
                }
            }
        }
        memberTeamCombo.setItems(FXCollections.observableArrayList(teams == null ? List.of() : teams));
        if (memberTeamCombo.getSelectionModel().isEmpty() && !memberTeamCombo.getItems().isEmpty()) {
            memberTeamCombo.getSelectionModel().selectFirst();
        }
    }

    private void refreshTrainingList() {
        Team selected = memberTeamCombo.getValue();
        if (selected == null) {
            trainingTable.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Training> trainings = trainingFacade.listUpcomingByTeam(selected.getId(), LocalDate.now());
        trainingTable.setItems(FXCollections.observableArrayList(trainings == null ? List.of() : trainings));
    }

    private void openMemberStats() {
        List<Team> teams = teamController.handleGetTeamsByMember(currentUser.getId());
        if (teams == null || teams.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune équipe", "Aucune équipe associée à votre compte.");
            return;
        }
        StatFrame statFrame = new StatFrame();
        statFrame.show(teams);
    }

    private void handleTrainingParticipation() {
        Training selected = trainingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            trainingMessageLabel.setText("Sélectionnez un entraînement.");
            return;
        }
        ParticipationStatus status = trainingStatusChoice.getValue();
        boolean ok = trainingFacade.markParticipation(selected.getId(), currentUser.getId(), status);
        trainingMessageLabel.setText(ok ? "Participation mise à jour." : "Impossible de mettre à jour.");
    }

    private String getMemberTeamName(int teamId) {
        String name = memberTeamNames.get(teamId);
        return name != null ? name : ("ID " + teamId);
    }

    private void createCommunicationView() {
        communicationView = new VBox(12);
        communicationView.setPadding(new Insets(30));
        Label title = new Label("Communication");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Button openChat = new Button("Ouvrir le chat");
        openChat.setOnAction(e -> new CommunicationFrame(new Stage()));
        communicationView.getChildren().addAll(title, openChat);
    }

    private void refreshEventList() {
        LocalDate start = eventStartDate.getValue();
        LocalDate end = eventEndDate.getValue();
        if (start == null || end == null) {
            eventMessageLabel.setText("Sélectionnez une période.");
            return;
        }
        List<Event> events = memberClubId > 0
                ? eventController.getEventsByClub(memberClubId)
                : eventController.getEventsByDateRange(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        if (events != null) {
            events = events.stream().filter(e -> {
                if (e == null || e.getDateDebut() == null) return false;
                if (start != null && e.getDateDebut().isBefore(start.atStartOfDay())) return false;
                if (end != null && e.getDateDebut().isAfter(end.atTime(LocalTime.MAX))) return false;
                return true;
            }).toList();
        }
        eventList.setItems(FXCollections.observableArrayList(events == null ? List.of() : events));
        eventMessageLabel.setText("");
    }

    private void handleRsvp() {
        Event selected = eventList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            eventMessageLabel.setText("Sélectionnez un événement.");
            return;
        }
        boolean ok = eventController.rsvpToEvent(selected.getId(), currentUser.getId(), eventRsvpChoice.getValue());
        eventMessageLabel.setText(ok ? "RSVP enregistré." : eventController.getLastError());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void refreshLicenceInfo() {
        licenceStatusBox.getChildren().clear();
        List<Licence> licences = LicenceFacade.getInstance().getLicencesByMembre(currentUser.getId());

        if (licences.isEmpty()) {
            licenceStatusBox.getChildren().add(new Label("Aucune licence enregistrée. Faites votre première demande ci-dessous."));
        } else {
            // Affichage de TOUTES les licences (Critère 7.4 du PDF)
            for (Licence l : licences) {
                HBox row = new HBox(15);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-alignment: CENTER_LEFT;");

                VBox details = new VBox(5);
                Label sportLabel = new Label(l.getSport().getNom() + " (" + l.getTypeLicence() + ")");
                sportLabel.setStyle("-fx-font-weight: bold;");

                Label statusLabel = new Label(l.getStatut().toString());
                String color = "#e67e22"; // Orange par défaut
                if (l.getStatut() == StatutLicence.ACTIVE) color = "#27ae60";
                if (l.getStatut() == StatutLicence.REFUSEE) color = "#c0392b";
                statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

                details.getChildren().addAll(sportLabel, statusLabel);

                Label dateLabel = new Label("Demandée le : " + l.getDateDemande());
                dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                row.getChildren().addAll(details, spacer, dateLabel);
                licenceStatusBox.getChildren().add(row);
            }
        }
    }

    private void setupClubTable() {
        TableColumn<Club, String> nameCol = new TableColumn<>("Nom du Club");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Club, String> typeCol = new TableColumn<>("Discipline");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        clubTable.getColumns().addAll(nameCol, typeCol);
        clubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void switchView(Button active, Button... inactive) {
        active.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        if (inactive == null) {
            return;
        }
        for (Button btn : inactive) {
            if (btn != null) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
            }
        }
    }

    private void refreshList() {
        try {
            List<Club> clubs = clubController.getAllClubs();
            clubTable.setItems(FXCollections.observableArrayList(clubs));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les clubs.");
        }
    }

    private void handleJoinRequest(Club club) {
        try {
            String role = joinRoleChoice.getValue();
            if (role == null || role.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez choisir un rôle.");
                return;
            }
            clubController.requestToJoinClub(club.getClubID(), currentUser.getId(), role);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande envoyée pour " + club.getName());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void handleLogout(Stage currentStage) {
        currentStage.close();
        new LoginFrame().start(new Stage());
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        return btn;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
