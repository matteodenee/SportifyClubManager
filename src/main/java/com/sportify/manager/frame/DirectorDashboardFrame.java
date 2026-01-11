package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.TeamController;
import com.sportify.manager.controllers.TypeSportController;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.MembershipRequest;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.User;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.StatutLicence;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.sportify.manager.controllers.TrainingController;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.Training;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import java.sql.SQLException;

public class DirectorDashboardFrame extends Application {
    private ClubController clubController;
    private TeamController teamController = TeamController.getInstance();
    private TypeSportController typeSportController = new TypeSportController();
    private TrainingController trainingController;
    private int directorClubId = -1;
    private User currentUser;

    // --- NAVIGATION ---
    private VBox membershipView, licenceView;
    private VBox teamView, trainingView, eventView, communicationView, equipmentView, typeEquipmentView;
    private StackPane contentArea;
    private Button btnRequests, btnLicences;
    private Button btnTeam, btnTraining, btnEvents, btnCommunication, btnEquipment, btnTypeEquipment;

    // --- TABLEAUX ---
    private TableView<MembershipRequest> requestTable;
    private TableView<Licence> licenceTable;
    private TableView<Team> teamTable;
    private TableView<User> teamPlayersTable;
    private TextField requestSearchField;
    private TextField licenceSearchField;
    private TextField teamNameField;
    private TextField teamCategoryField;
    private ComboBox<Club> teamClubCombo;
    private ComboBox<User> teamCoachCombo;
    private ComboBox<TypeSport> teamSportCombo;
    private TextField teamPlayerIdField;
    private Label teamMessageLabel;
    private int currentTeamId = 0;
    private Label requestCountLabel;
    private Label licenceCountLabel;
    private java.util.List<MembershipRequest> requestCache = java.util.Collections.emptyList();
    private java.util.List<Licence> licenceCache = java.util.Collections.emptyList();



    public DirectorDashboardFrame(User user) {
        this.currentUser = user;
    }

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
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("DIRECTION");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        btnRequests = createMenuButton("📩 Adhésions Clubs", true);
        btnLicences = createMenuButton("📜 Licences Membres", false);
        btnTeam = createMenuButton("👥 Team Management", false);
        btnTraining = createMenuButton("🏋️ Training Management", false);
        btnEvents = createMenuButton("📅 Event Management", false);
        btnCommunication = createMenuButton("💬 Communication Management", false);
        btnEquipment = createMenuButton("🧰 Equipement Management", false);
        btnTypeEquipment = createMenuButton("🏷️ Type Equipement Management", false);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(
                menuLabel,
                new Separator(),
                btnRequests,
                btnLicences,
                btnTeam,
                btnTraining,
                btnEvents,
                btnCommunication,
                btnEquipment,
                btnTypeEquipment,
                btnLogout
        );

        // --- VUES ---
        createMembershipView();
        createLicenceView();
        createTeamView();
        createPlaceholders();

        contentArea = new StackPane(
                membershipView,
                licenceView,
                teamView,
                trainingView,
                eventView,
                communicationView,
                equipmentView,
                typeEquipmentView
        );
        licenceView.setVisible(false);
        teamView.setVisible(false);
        trainingView.setVisible(false);
        eventView.setVisible(false);
        communicationView.setVisible(false);
        equipmentView.setVisible(false);
        typeEquipmentView.setVisible(false);

        // --- ACTIONS ---
        btnRequests.setOnAction(e -> { switchView(membershipView, btnRequests); refreshMembershipTable(); });
        btnLicences.setOnAction(e -> { switchView(licenceView, btnLicences); refreshLicenceTable(); });
        btnTeam.setOnAction(e -> {
            switchView(teamView, btnTeam);
            refreshTeamChoices();
        });
        btnTraining.setOnAction(e -> switchView(trainingView, btnTraining));
        btnEvents.setOnAction(e -> switchView(eventView, btnEvents));
        btnCommunication.setOnAction(e -> switchView(communicationView, btnCommunication));
        btnEquipment.setOnAction(e -> switchView(equipmentView, btnEquipment));
        btnTypeEquipment.setOnAction(e -> switchView(typeEquipmentView, btnTypeEquipment));
        btnLogout.setOnAction(e -> { primaryStage.close(); new LoginFrame().start(new Stage()); });

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Sportify - Espace Direction");
        primaryStage.setScene(scene);
        refreshMembershipTable();
        primaryStage.show();
    }

    // ==========================================
    // VUE 1 : ADHÉSIONS CLUBS
    // ==========================================
    private void createMembershipView() {
        membershipView = new VBox(20);
        membershipView.setPadding(new Insets(30));

        Label title = new Label("DEMANDES D'ADHÉSION AUX CLUBS");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox searchBar = new HBox(10);
        requestSearchField = new TextField();
        requestSearchField.setPromptText("Rechercher (membre/club)...");
        requestCountLabel = new Label("0 demande(s)");
        requestCountLabel.setStyle("-fx-text-fill: #7f8c8d;");
        searchBar.getChildren().addAll(requestSearchField, requestCountLabel);

        requestTable = new TableView<>();
        setupMembershipTableColumns();

        Button btnApprove = new Button("✔ Valider l'Adhésion");
        styleButton(btnApprove, "#27ae60");
        btnApprove.setOnAction(e -> handleMembershipAction());

        membershipView.getChildren().addAll(title, searchBar, requestTable, btnApprove);

        requestSearchField.textProperty().addListener((obs, old, val) -> applyMembershipFilter());
    }

    private void setupMembershipTableColumns() {
        TableColumn<MembershipRequest, String> userCol = new TableColumn<>("Candidat");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        TableColumn<MembershipRequest, String> clubCol = new TableColumn<>("Club Visé");
        clubCol.setCellValueFactory(new PropertyValueFactory<>("clubName"));

        requestTable.getColumns().addAll(userCol, clubCol);
        requestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ==========================================
    // VUE 2 : LICENCES
    // ==========================================
    private void createLicenceView() {
        licenceView = new VBox(20);
        licenceView.setPadding(new Insets(30));

        Label title = new Label("VALIDATION DES LICENCES");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox searchBar = new HBox(10);
        licenceSearchField = new TextField();
        licenceSearchField.setPromptText("Rechercher (membre/sport/type)...");
        licenceCountLabel = new Label("0 licence(s)");
        licenceCountLabel.setStyle("-fx-text-fill: #7f8c8d;");
        searchBar.getChildren().addAll(licenceSearchField, licenceCountLabel);

        licenceTable = new TableView<>();
        setupLicenceTableColumns();

        Button btnApprove = new Button("✅ Octroyer la Licence");
        styleButton(btnApprove, "#2ecc71");
        btnApprove.setOnAction(e -> handleLicenceAction());

        licenceView.getChildren().addAll(title, searchBar, licenceTable, btnApprove);

        licenceSearchField.textProperty().addListener((obs, old, val) -> applyLicenceFilter());
    }

    // ==========================================
    // VUE 3 : TEAM MANAGEMENT
    // ==========================================
    private void createTeamView() {
        teamView = new VBox(20);
        teamView.setPadding(new Insets(30));

        Label title = new Label("GESTION DES ÉQUIPES");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dcdde1;");

        teamNameField = new TextField();
        teamCategoryField = new TextField();
        teamClubCombo = new ComboBox<>();
        teamClubCombo.setPromptText("Club");
        teamCoachCombo = new ComboBox<>();
        teamCoachCombo.setPromptText("Coach");
        teamSportCombo = new ComboBox<>();
        teamSportCombo.setPromptText("Type Sport");
        teamPlayerIdField = new TextField();
        teamMessageLabel = new Label();
        teamMessageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        formGrid.add(new Label("Nom :"), 0, 0);
        formGrid.add(teamNameField, 1, 0);
        formGrid.add(new Label("Catégorie :"), 2, 0);
        formGrid.add(teamCategoryField, 3, 0);

        formGrid.add(new Label("Club :"), 0, 1);
        formGrid.add(teamClubCombo, 1, 1);
        formGrid.add(new Label("Coach :"), 2, 1);
        formGrid.add(teamCoachCombo, 3, 1);

        formGrid.add(new Label("Type Sport :"), 0, 2);
        formGrid.add(teamSportCombo, 1, 2);

        teamCoachCombo.setCellFactory(list -> new ListCell<>() {
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
        teamCoachCombo.setButtonCell(new ListCell<>() {
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

        teamClubCombo.setOnAction(e -> {
            refreshTeamCoaches();
            if (teamClubCombo.getValue() != null) {
                refreshTeamsByClub(teamClubCombo.getValue().getClubID());
            } else {
                teamTable.setItems(FXCollections.observableArrayList());
                teamPlayersTable.setItems(FXCollections.observableArrayList());
            }
        });

        Button btnCreate = new Button("➕ Créer");
        styleButton(btnCreate, "#2ecc71");
        Button btnUpdate = new Button("💾 Modifier");
        styleButton(btnUpdate, "#f1c40f");
        Button btnDelete = new Button("🗑 Supprimer");
        styleButton(btnDelete, "#e74c3c");
        Button btnClear = new Button("🧹 Vider");
        styleButton(btnClear, "#95a5a6");

        HBox actions = new HBox(10, btnCreate, btnUpdate, btnDelete, btnClear);
        formGrid.add(actions, 1, 3, 3, 1);

        HBox playerActions = new HBox(10);
        Button btnAddPlayer = new Button("➕ Ajouter joueur");
        styleButton(btnAddPlayer, "#3498db");
        Button btnRemovePlayer = new Button("➖ Retirer joueur");
        styleButton(btnRemovePlayer, "#e67e22");
        playerActions.getChildren().addAll(new Label("Player ID :"), teamPlayerIdField, btnAddPlayer, btnRemovePlayer);

        teamTable = new TableView<>();
        setupTeamTable();

        teamPlayersTable = new TableView<>();
        setupTeamPlayersTable();

        teamView.getChildren().addAll(
                title,
                formGrid,
                teamMessageLabel,
                playerActions,
                new Label("Équipes du club :"),
                teamTable,
                new Label("Joueurs de l'équipe :"),
                teamPlayersTable
        );

        btnCreate.setOnAction(e -> handleCreateTeam());
        btnUpdate.setOnAction(e -> handleUpdateTeam());
        btnDelete.setOnAction(e -> handleDeleteTeam());
        btnClear.setOnAction(e -> clearTeamFields());
        btnAddPlayer.setOnAction(e -> handleAddPlayer());
        btnRemovePlayer.setOnAction(e -> handleRemovePlayer());
    }

    private void createPlaceholders() {
        trainingView = createPlaceholderView("Training Management", "Ce module sera ajouté prochainement.");
        eventView = createPlaceholderView("Event Management", "Ce module sera ajouté prochainement.");
        communicationView = createPlaceholderView("Communication Management", "Ce module sera ajouté prochainement.");
        equipmentView = createPlaceholderView("Equipement Management", "Ce module sera ajouté prochainement.");
        typeEquipmentView = createPlaceholderView("Type Equipement Management", "Ce module sera ajouté prochainement.");
    }

    private void setupLicenceTableColumns() {
        TableColumn<Licence, String> memberCol = new TableColumn<>("Membre");
        memberCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMembre().getName()));

        TableColumn<Licence, String> sportCol = new TableColumn<>("Discipline");
        sportCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSport().getNom()));

        TableColumn<Licence, String> typeCol = new TableColumn<>("Type de Licence");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeLicence"));

        licenceTable.getColumns().addAll(memberCol, sportCol, typeCol);
        licenceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupTeamTable() {
        TableColumn<Team, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Team, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Team, String> catCol = new TableColumn<>("Catégorie");
        catCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        TableColumn<Team, Integer> clubCol = new TableColumn<>("Club ID");
        clubCol.setCellValueFactory(new PropertyValueFactory<>("clubId"));

        TableColumn<Team, Integer> coachCol = new TableColumn<>("Coach ID");
        coachCol.setCellValueFactory(new PropertyValueFactory<>("coachId"));

        TableColumn<Team, Integer> sportCol = new TableColumn<>("Sport ID");
        sportCol.setCellValueFactory(new PropertyValueFactory<>("typeSportId"));

        teamTable.getColumns().addAll(idCol, nameCol, catCol, clubCol, coachCol, sportCol);
        teamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        teamTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;
            if (teamClubCombo.getItems().isEmpty() || teamCoachCombo.getItems().isEmpty() || teamSportCombo.getItems().isEmpty()) {
                refreshTeamChoices();
            }
            currentTeamId = sel.getId();
            teamNameField.setText(sel.getNom());
            teamCategoryField.setText(sel.getCategorie());
            selectTeamClub(sel.getClubId());
            selectTeamCoach(sel.getCoachId());
            selectTeamSport(sel.getTypeSportId());
            refreshTeamPlayers(sel.getId());
        });
    }

    private void setupTeamPlayersTable() {
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        teamPlayersTable.getColumns().addAll(idCol, nameCol, roleCol);
        teamPlayersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // --- LOGIQUE MÉTIER ---

    private void refreshMembershipTable() {
        try {
            requestCache = clubController.getRequestsForDirector(currentUser.getId());
            applyMembershipFilter();
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les adhésions.");
        }
    }

    private void refreshLicenceTable() {
        licenceCache = LicenceFacade.getInstance().getLicencesByStatut(StatutLicence.EN_ATTENTE);
        applyLicenceFilter();
    }

    private void handleMembershipAction() {
        MembershipRequest sel = requestTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                clubController.approveRequest(sel.getRequestId());
                refreshMembershipTable();
                showInfo("Adhésion validée", "Le membre a été ajouté au club.");
            } catch (SQLException e) {
                showError("Erreur", e.getMessage());
            }
        }
    }

    private void handleLicenceAction() {
        Licence sel = licenceTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            LicenceFacade.getInstance().validerLicence(sel.getId(), true, "Validé par le directeur : " + currentUser.getName());
            refreshLicenceTable();
            showInfo("Licence validée", "La licence a été accordée.");
        }
    }

    private void handleCreateTeam() {
        try {
            String nom = teamNameField.getText();
            String cat = teamCategoryField.getText();
            Club club = teamClubCombo.getValue();
            if (club == null) {
                teamMessageLabel.setText("Sélectionnez un club.");
                return;
            }
            int clubId = club.getClubID();
            User coach = teamCoachCombo.getValue();
            String coachId = coach == null ? null : coach.getId();
            TypeSport sport = teamSportCombo.getValue();
            Integer sportId = sport == null ? null : sport.getId();
            boolean ok = teamController.handleCreateTeam(nom, cat, clubId, coachId, sportId);
            if (!ok) {
                teamMessageLabel.setText(teamController.getLastError());
                return;
            }
            teamMessageLabel.setText("Équipe créée.");
            refreshTeamsByClub(clubId);
            clearTeamFields();
        } catch (Exception e) {
            teamMessageLabel.setText("Saisie invalide.");
        }
    }

    private void handleUpdateTeam() {
        if (currentTeamId == 0) return;
        try {
            if (teamClubCombo.getValue() == null) {
                teamMessageLabel.setText("Sélectionnez un club.");
                return;
            }
            Team team = new Team(
                    currentTeamId,
                    teamNameField.getText(),
                    teamCategoryField.getText(),
                    teamClubCombo.getValue().getClubID(),
                    teamCoachCombo.getValue() == null ? null : teamCoachCombo.getValue().getId(),
                    teamSportCombo.getValue() == null ? null : teamSportCombo.getValue().getId()
            );
            boolean ok = teamController.handleUpdateTeam(team);
            if (!ok) {
                teamMessageLabel.setText(teamController.getLastError());
                return;
            }
            teamMessageLabel.setText("Équipe mise à jour.");
            refreshTeamsByClub(team.getClubId());
        } catch (Exception e) {
            teamMessageLabel.setText("Saisie invalide.");
        }
    }

    private void handleDeleteTeam() {
        if (currentTeamId == 0) return;
        try {
            boolean ok = teamController.handleDeleteTeam(currentTeamId);
            if (!ok) {
                teamMessageLabel.setText(teamController.getLastError());
                return;
            }
            teamMessageLabel.setText("Équipe supprimée.");
            if (teamClubCombo.getValue() != null) {
                refreshTeamsByClub(teamClubCombo.getValue().getClubID());
            } else {
                teamTable.setItems(FXCollections.observableArrayList());
            }
            clearTeamFields();
            teamPlayersTable.setItems(FXCollections.observableArrayList());
        } catch (Exception e) {
            teamMessageLabel.setText("Saisie invalide.");
        }
    }

    private void handleAddPlayer() {
        if (currentTeamId == 0) return;
        String userId = teamPlayerIdField.getText();
        if (userId == null || userId.isBlank()) return;
        boolean ok = teamController.handleAddPlayer(currentTeamId, userId.trim());
        if (!ok) {
            teamMessageLabel.setText(teamController.getLastError());
            return;
        }
        refreshTeamPlayers(currentTeamId);
    }

    private void handleRemovePlayer() {
        if (currentTeamId == 0) return;
        String userId = teamPlayerIdField.getText();
        if (userId == null || userId.isBlank()) return;
        boolean ok = teamController.handleRemovePlayer(currentTeamId, userId.trim());
        if (!ok) {
            teamMessageLabel.setText(teamController.getLastError());
            return;
        }
        refreshTeamPlayers(currentTeamId);
    }

    private void refreshTeamsByClub(int clubId) {
        java.util.List<Team> teams = teamController.handleGetTeams(clubId);
        if (teams == null) {
            teamTable.setItems(FXCollections.observableArrayList());
            teamMessageLabel.setText(teamController.getLastError());
            return;
        }
        teamTable.setItems(FXCollections.observableArrayList(teams));
    }

    private void refreshTeamPlayers(int teamId) {
        java.util.List<User> players = teamController.handleGetPlayers(teamId);
        if (players == null) {
            teamPlayersTable.setItems(FXCollections.observableArrayList());
            return;
        }
        teamPlayersTable.setItems(FXCollections.observableArrayList(players));
    }

    private void refreshTeamChoices() {
        try {
            java.util.List<Club> clubs = clubController.getClubsByManager(currentUser.getId());
            teamClubCombo.setItems(FXCollections.observableArrayList(clubs));
            if (!clubs.isEmpty() && teamClubCombo.getValue() == null) {
                teamClubCombo.getSelectionModel().select(0);
                refreshTeamCoaches();
                refreshTeamsByClub(clubs.get(0).getClubID());
            }
            teamClubCombo.setDisable(clubs.size() == 1);
        } catch (SQLException e) {
            teamClubCombo.setItems(FXCollections.observableArrayList());
        }

        refreshTeamCoaches();

        java.util.List<TypeSport> sports = typeSportController.handleGetAllTypeSports();
        if (sports == null) {
            teamSportCombo.setItems(FXCollections.observableArrayList());
        } else {
            teamSportCombo.setItems(FXCollections.observableArrayList(sports));
        }
    }

    private void refreshTeamCoaches() {
        Club club = teamClubCombo.getValue();
        if (club == null) {
            teamCoachCombo.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            java.util.List<User> coaches = PostgresUserDAO.getInstance().getCoachesByClub(club.getClubID());
            teamCoachCombo.setItems(FXCollections.observableArrayList(coaches));
        } catch (SQLException e) {
            teamCoachCombo.setItems(FXCollections.observableArrayList());
        }
    }

    private void selectTeamClub(int clubId) {
        for (Club club : teamClubCombo.getItems()) {
            if (club.getClubID() == clubId) {
                teamClubCombo.getSelectionModel().select(club);
                return;
            }
        }
        teamClubCombo.getSelectionModel().clearSelection();
    }

    private void selectTeamCoach(String coachId) {
        if (coachId == null) {
            teamCoachCombo.getSelectionModel().clearSelection();
            return;
        }
        for (User coach : teamCoachCombo.getItems()) {
            if (coachId.equals(coach.getId())) {
                teamCoachCombo.getSelectionModel().select(coach);
                return;
            }
        }
        teamCoachCombo.getSelectionModel().clearSelection();
    }

    private void selectTeamSport(Integer sportId) {
        if (sportId == null) {
            teamSportCombo.getSelectionModel().clearSelection();
            return;
        }
        for (TypeSport sport : teamSportCombo.getItems()) {
            if (sport.getId() == sportId) {
                teamSportCombo.getSelectionModel().select(sport);
                return;
            }
        }
        teamSportCombo.getSelectionModel().clearSelection();
    }

    private void clearTeamFields() {
        teamNameField.clear();
        teamCategoryField.clear();
        teamClubCombo.getSelectionModel().clearSelection();
        teamCoachCombo.getSelectionModel().clearSelection();
        teamSportCombo.getSelectionModel().clearSelection();
        teamPlayerIdField.clear();
        currentTeamId = 0;
    }

    private void applyMembershipFilter() {
        String q = requestSearchField.getText() == null ? "" : requestSearchField.getText().trim().toLowerCase();
        java.util.List<MembershipRequest> filtered = new java.util.ArrayList<>();
        for (MembershipRequest r : requestCache) {
            String member = r.getUserName() == null ? "" : r.getUserName().toLowerCase();
            String club = r.getClubName() == null ? "" : r.getClubName().toLowerCase();
            if (q.isEmpty() || member.contains(q) || club.contains(q)) {
                filtered.add(r);
            }
        }
        requestTable.setItems(FXCollections.observableArrayList(filtered));
        requestCountLabel.setText(filtered.size() + " demande(s)");
    }

    private void applyLicenceFilter() {
        String q = licenceSearchField.getText() == null ? "" : licenceSearchField.getText().trim().toLowerCase();
        java.util.List<Licence> filtered = new java.util.ArrayList<>();
        for (Licence l : licenceCache) {
            String member = l.getMembre() == null ? "" : l.getMembre().getName().toLowerCase();
            String sport = l.getSport() == null ? "" : l.getSport().getNom().toLowerCase();
            String type = l.getTypeLicence() == null ? "" : l.getTypeLicence().toString().toLowerCase();
            if (q.isEmpty() || member.contains(q) || sport.contains(q) || type.contains(q)) {
                filtered.add(l);
            }
        }
        licenceTable.setItems(FXCollections.observableArrayList(filtered));
        licenceCountLabel.setText(filtered.size() + " licence(s)");
    }

    // --- UI UTILS ---

    private void switchView(VBox view, Button btn) {
        membershipView.setVisible(false);
        licenceView.setVisible(false);
        teamView.setVisible(false);
        trainingView.setVisible(false);
        eventView.setVisible(false);
        communicationView.setVisible(false);
        equipmentView.setVisible(false);
        typeEquipmentView.setVisible(false);
        view.setVisible(true);
        setActiveButton(btn);
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(12));
        btn.setStyle("-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        return btn;
    }

    private void setActiveButton(Button active) {
        btnRequests.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnLicences.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnTeam.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnTraining.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnEvents.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnCommunication.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnEquipment.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btnTypeEquipment.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        active.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 5;");
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-cursor: hand; -fx-background-radius: 5;");
    }

    private VBox createPlaceholderView(String titleText, String bodyText) {
        VBox view = new VBox(12);
        view.setPadding(new Insets(30));

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label body = new Label(bodyText);
        body.setStyle("-fx-text-fill: #7f8c8d;");

        view.getChildren().addAll(title, body);
        return view;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
