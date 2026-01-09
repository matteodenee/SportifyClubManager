package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.services.MembershipRequest;
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

import java.sql.SQLException;

public class DirectorDashboardFrame extends Application {
    private ClubController clubController;
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
    private TextField requestSearchField;
    private TextField licenceSearchField;
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
        btnTeam.setOnAction(e -> switchView(teamView, btnTeam));
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

    private void createPlaceholders() {
        teamView = createPlaceholderView("Team Management", "Ce module sera ajouté prochainement.");
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
