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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class DirectorDashboardFrame extends Application {
    private ClubController clubController;
    private User currentUser;

    // Éléments pour les Adhésions Clubs
    private TableView<MembershipRequest> requestTable;
    private VBox membershipView;

    // Éléments pour les Licences
    private TableView<Licence> licenceTable;
    private VBox licenceView;

    private StackPane contentArea; // Zone centrale interchangeable

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
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("DIRECTION");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnRequests = createMenuButton("📩 Adhésions", true);
        Button btnLicences = createMenuButton("📜 Licences", false);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnRequests, btnLicences, btnLogout);

        // --- PRÉPARATION DES VUES (Adhésions et Licences) ---
        createMembershipView();
        createLicenceView();

        // StackPane permet d'empiler les vues et d'afficher seulement celle choisie
        contentArea = new StackPane(membershipView, licenceView);
        licenceView.setVisible(false); // On cache les licences au démarrage

        // --- ACTIONS SIDEBAR ---
        btnRequests.setOnAction(e -> {
            updateActiveButton(btnRequests, btnLicences);
            membershipView.setVisible(true);
            licenceView.setVisible(false);
            refreshMembershipTable();
        });

        btnLicences.setOnAction(e -> {
            updateActiveButton(btnLicences, btnRequests);
            membershipView.setVisible(false);
            licenceView.setVisible(true);
            refreshLicenceTable();
        });

        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        // --- ASSEMBLAGE FINAL ---
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Sportify - Espace Direction");
        primaryStage.setScene(scene);

        refreshMembershipTable();
        primaryStage.show();
    }

    // ==========================
    // VUE 1 : ADHÉSIONS CLUBS
    // ==========================
    private void createMembershipView() {
        membershipView = new VBox(20);
        membershipView.setPadding(new Insets(30));

        Label title = new Label("Demandes d'adhésion aux clubs");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        requestTable = new TableView<>();
        setupMembershipTableColumns();

        Button btnApprove = new Button("✔ Approuver");
        Button btnReject = new Button("✖ Refuser");
        styleButton(btnApprove, "#27ae60");
        styleButton(btnReject, "#e74c3c");

        btnApprove.setOnAction(e -> handleMembershipAction(true));
        btnReject.setOnAction(e -> handleMembershipAction(false));

        HBox actions = new HBox(15, new Label("Actions :"), btnApprove, btnReject);
        actions.setAlignment(Pos.CENTER_LEFT);

        membershipView.getChildren().addAll(title, requestTable, actions);
    }

    private void setupMembershipTableColumns() {
        TableColumn<MembershipRequest, String> userCol = new TableColumn<>("Candidat");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<MembershipRequest, String> clubCol = new TableColumn<>("Club Visé");
        clubCol.setCellValueFactory(new PropertyValueFactory<>("clubName"));

        TableColumn<MembershipRequest, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        requestTable.getColumns().addAll(userCol, clubCol, statusCol);
        requestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ==========================
    // VUE 2 : GESTION DES LICENCES
    // ==========================
    private void createLicenceView() {
        licenceView = new VBox(20);
        licenceView.setPadding(new Insets(30));

        Label title = new Label("Validation des Licences Sportives");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        licenceTable = new TableView<>();
        setupLicenceTableColumns();

        Button btnApproveLicence = new Button("✅ Valider Licence");
        Button btnRejectLicence = new Button("❌ Refuser");
        styleButton(btnApproveLicence, "#2ecc71");
        styleButton(btnRejectLicence, "#e67e22");

        btnApproveLicence.setOnAction(e -> handleLicenceAction(true));
        btnRejectLicence.setOnAction(e -> handleLicenceAction(false));

        HBox actions = new HBox(15, new Label("Actions Licence :"), btnApproveLicence, btnRejectLicence);
        actions.setAlignment(Pos.CENTER_LEFT);

        licenceView.getChildren().addAll(title, licenceTable, actions);
    }

    private void setupLicenceTableColumns() {
        // Colonne pour extraire le nom du membre de l'objet User
        TableColumn<Licence, String> memberCol = new TableColumn<>("Membre");
        memberCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMembre().getName()));

        TableColumn<Licence, String> sportCol = new TableColumn<>("Sport");
        sportCol.setCellValueFactory(new PropertyValueFactory<>("sport"));

        TableColumn<Licence, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeLicence"));

        TableColumn<Licence, String> dateCol = new TableColumn<>("Date Demande");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateDemande"));

        licenceTable.getColumns().addAll(memberCol, sportCol, typeCol, dateCol);
        licenceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ==========================
    // LOGIQUE ET REFRESH
    // ==========================
    private void refreshMembershipTable() {
        try {
            List<MembershipRequest> requests = clubController.getRequestsForDirector(currentUser.getId());
            requestTable.setItems(FXCollections.observableArrayList(requests));
        } catch (SQLException e) { showError("Erreur", "Impossible de charger les adhésions."); }
    }

    private void refreshLicenceTable() {
        // On appelle ta Facade
        List<Licence> list = LicenceFacade.getInstance().getLicencesByStatut(StatutLicence.EN_ATTENTE);
        licenceTable.setItems(FXCollections.observableArrayList(list));
    }

    private void handleMembershipAction(boolean approve) {
        MembershipRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Sélection", "Choisissez une demande d'adhésion."); return; }
        try {
            if (approve) clubController.approveRequest(selected.getRequestId());
            else clubController.rejectRequest(selected.getRequestId());
            refreshMembershipTable();
        } catch (SQLException e) { showError("Erreur SQL", e.getMessage()); }
    }

    private void handleLicenceAction(boolean approve) {
        Licence selected = licenceTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Sélection", "Choisissez une licence."); return; }

        // Appel à ta Facade -> Manager -> DAO
        LicenceFacade.getInstance().validerLicence(selected.getId(), approve, "Approuvé par le directeur");
        refreshLicenceTable();
    }

    private void handleLogout(Stage currentStage) {
        currentStage.close();
        new LoginFrame().start(new Stage());
    }

    // ==========================
    // UTILITAIRES DESIGN
    // ==========================
    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(12));
        btn.setStyle("-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        return btn;
    }

    private void updateActiveButton(Button active, Button inactive) {
        active.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand; -fx-background-radius: 5;");
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}