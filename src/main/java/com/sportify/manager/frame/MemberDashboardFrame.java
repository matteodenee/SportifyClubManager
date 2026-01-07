package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.controllers.LicenceController;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.facade.TypeSportFacade; // Import de la facade de ton ami
import com.sportify.manager.services.Club;
import com.sportify.manager.services.User;
import com.sportify.manager.services.TypeSport; // Import de l'objet Sport
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.TypeLicence;
import javafx.application.Application;
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

public class MemberDashboardFrame extends Application {
    private ClubController clubController;
    private LicenceController licenceController;
    private User currentUser;

    private TableView<Club> clubTable;
    private VBox clubView;
    private VBox licenceView;
    private StackPane contentArea;
    private VBox licenceStatusBox;

    public MemberDashboardFrame(User user) {
        this.currentUser = user;
        this.licenceController = new LicenceController();
        this.licenceController.setCurrentUser(user);
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

        Label menuLabel = new Label("MON ESPACE");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnClubs = createMenuButton("🔍 Parcourir Clubs", true);
        Button btnLicence = createMenuButton("📜 Ma Licence", false);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnLicence, btnLogout);

        createClubView();
        createLicenceView();

        contentArea = new StackPane(clubView, licenceView);
        licenceView.setVisible(false);

        btnClubs.setOnAction(e -> {
            switchView(btnClubs, btnLicence);
            clubView.setVisible(true);
            licenceView.setVisible(false);
            refreshList();
        });

        btnLicence.setOnAction(e -> {
            switchView(btnLicence, btnClubs);
            clubView.setVisible(false);
            licenceView.setVisible(true);
            refreshLicenceInfo();
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

        Button joinButton = new Button("Demander à rejoindre");
        styleButton(joinButton, "#3498db");
        joinButton.setOnAction(e -> {
            Club selected = clubTable.getSelectionModel().getSelectedItem();
            if (selected != null) handleJoinRequest(selected);
            else showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez choisir un club.");
        });

        clubView.getChildren().addAll(welcomeLabel, new Label("Clubs disponibles :"), clubTable, joinButton);
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

        // --- FORMULAIRE DE DEMANDE MIS À JOUR ---
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        Label formTitle = new Label("Nouvelle demande de licence");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // REMPLACEMENT : TextField -> ComboBox de TypeSport
        ComboBox<TypeSport> sportCombo = new ComboBox<>();
        sportCombo.setPromptText("Sélectionnez un sport");
        sportCombo.setMaxWidth(Double.MAX_VALUE);

        // Chargement des sports via la Facade de ton ami
        try {
            List<TypeSport> sports = TypeSportFacade.getInstance().getAllTypeSports();
            sportCombo.setItems(FXCollections.observableArrayList(sports));
        } catch (Exception e) {
            System.err.println("Erreur chargement sports : " + e.getMessage());
        }

        ComboBox<TypeLicence> typeCombo = new ComboBox<>(FXCollections.observableArrayList(TypeLicence.values()));
        typeCombo.setPromptText("Type (JOUEUR, COACH...)");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        Button submitBtn = new Button("Envoyer la demande");
        styleButton(submitBtn, "#2ecc71");

        submitBtn.setOnAction(e -> {
            TypeSport selectedSport = sportCombo.getValue();
            TypeLicence selectedType = typeCombo.getValue();

            if (selectedSport != null && selectedType != null) {
                licenceController.onDemandeLicence(selectedSport, selectedType);
                refreshLicenceInfo();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre demande pour le " + selectedSport.getNom() + " a été envoyée.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez sélectionner un sport et un type.");
            }
        });

        formBox.getChildren().addAll(formTitle, new Label("Discipline disponible :"), sportCombo, new Label("Type :"), typeCombo, submitBtn);
        licenceView.getChildren().addAll(title, new Label("Statut de votre licence actuelle :"), licenceStatusBox, new Separator(), formBox);
    }

    private void refreshLicenceInfo() {
        licenceStatusBox.getChildren().clear();
        List<Licence> licences = LicenceFacade.getInstance().getLicencesByMembre(currentUser.getId());

        if (licences.isEmpty()) {
            licenceStatusBox.getChildren().add(new Label("Aucune licence enregistrée."));
        } else {
            Licence l = licences.get(licences.size() - 1);
            Label statusLabel = new Label("STATUT : " + l.getStatut());
            statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                    (l.getStatut().toString().equals("ACTIVE") ? "#27ae60" : "#e67e22"));

            licenceStatusBox.getChildren().addAll(
                    new Label("ID : " + l.getId()),
                    // l.getSport() appelle le toString() de TypeSport qui renvoie le nom
                    new Label("Sport : " + l.getSport()),
                    new Label("Type : " + l.getTypeLicence()),
                    statusLabel,
                    new Label("Note Admin : " + (l.getCommentaireAdmin() == null || l.getCommentaireAdmin().isEmpty() ? "Aucune" : l.getCommentaireAdmin()))
            );
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

    private void switchView(Button active, Button inactive) {
        active.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
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
            clubController.requestToJoinClub(club.getClubID(), currentUser.getId());
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