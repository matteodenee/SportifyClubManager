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
    private StackPane contentArea;
    private Button btnRequests, btnLicences;

    // --- TABLEAUX ---
    private TableView<MembershipRequest> requestTable;
    private TableView<Licence> licenceTable;

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
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnRequests, btnLicences, btnLogout);

        // --- VUES ---
        createMembershipView();
        createLicenceView();

        contentArea = new StackPane(membershipView, licenceView);
        licenceView.setVisible(false);

        // --- ACTIONS ---
        btnRequests.setOnAction(e -> { switchView(membershipView, btnRequests); refreshMembershipTable(); });
        btnLicences.setOnAction(e -> { switchView(licenceView, btnLicences); refreshLicenceTable(); });
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

        requestTable = new TableView<>();
        setupMembershipTableColumns();

        Button btnApprove = new Button("✔ Valider l'Adhésion");
        styleButton(btnApprove, "#27ae60");
        btnApprove.setOnAction(e -> handleMembershipAction());

        membershipView.getChildren().addAll(title, requestTable, btnApprove);
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

        licenceTable = new TableView<>();
        setupLicenceTableColumns();

        Button btnApprove = new Button("✅ Octroyer la Licence");
        styleButton(btnApprove, "#2ecc71");
        btnApprove.setOnAction(e -> handleLicenceAction());

        licenceView.getChildren().addAll(title, licenceTable, btnApprove);
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
            requestTable.setItems(FXCollections.observableArrayList(clubController.getRequestsForDirector(currentUser.getId())));
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les adhésions.");
        }
    }

    private void refreshLicenceTable() {
        licenceTable.setItems(FXCollections.observableArrayList(LicenceFacade.getInstance().getLicencesByStatut(StatutLicence.EN_ATTENTE)));
    }

    private void handleMembershipAction() {
        MembershipRequest sel = requestTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                clubController.approveRequest(sel.getRequestId());
                refreshMembershipTable();
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
        }
    }

    // --- UI UTILS ---

    private void switchView(VBox view, Button btn) {
        membershipView.setVisible(false);
        licenceView.setVisible(false);
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
        active.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-radius: 5;");
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-cursor: hand; -fx-background-radius: 5;");
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}