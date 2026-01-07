package com.sportify.manager.frame;

import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.services.Club;
import com.sportify.manager.services.User;
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
    private User currentUser;
    private TableView<Club> clubTable;

    public MemberDashboardFrame(User user) {
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

        // --- SIDEBAR (Identité Sportify) ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("MON ESPACE");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button btnClubs = createMenuButton("🔍 Parcourir Clubs", true);
        Button btnLogout = createMenuButton("🚪 Déconnexion", false);

        sidebar.getChildren().addAll(menuLabel, new Separator(), btnClubs, btnLogout);

        // --- MAIN CONTENT ---
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));

        // En-tête de bienvenue
        VBox header = new VBox(5);
        Label welcomeLabel = new Label("Bonjour, " + currentUser.getName());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        Label subLabel = new Label("Trouvez et rejoignez les clubs de sport qui vous passionnent.");
        subLabel.setStyle("-fx-text-fill: #7f8c8d;");
        header.getChildren().addAll(welcomeLabel, subLabel);

        // Tableau des clubs
        clubTable = new TableView<>();
        setupTable();

        // Zone d'action (Bouton rejoindre)
        HBox actionArea = new HBox(15);
        actionArea.setAlignment(Pos.CENTER_RIGHT);

        Button joinButton = new Button("Demander à rejoindre");
        styleButton(joinButton, "#3498db");

        joinButton.setOnAction(e -> {
            Club selected = clubTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleJoinRequest(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez choisir un club dans la liste.");
            }
        });

        actionArea.getChildren().add(joinButton);

        mainContent.getChildren().addAll(header, clubTable, actionArea);
        VBox.setVgrow(clubTable, Priority.ALWAYS);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        // Action déconnexion
        btnLogout.setOnAction(e -> handleLogout(primaryStage));

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Sportify - Espace Membre");
        primaryStage.setScene(scene);

        refreshList();
        primaryStage.show();
    }

    private void setupTable() {
        TableColumn<Club, String> nameCol = new TableColumn<>("Nom du Club");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Club, String> typeCol = new TableColumn<>("Discipline");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Club, Integer> countCol = new TableColumn<>("Membres Actuels");
        countCol.setCellValueFactory(new PropertyValueFactory<>("currentMemberCount"));

        clubTable.getColumns().addAll(nameCol, typeCol, countCol);
        clubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clubTable.setStyle("-fx-background-radius: 5;");
    }

    private void handleLogout(Stage currentStage) {
        currentStage.close();
        LoginFrame loginFrame = new LoginFrame();
        try {
            loginFrame.start(new Stage());
        } catch (Exception e) { e.printStackTrace(); }
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
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre demande pour rejoindre '" + club.getName() + "' a été envoyée !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Action impossible", e.getMessage());
        }
    }

    // --- UTILITAIRES DESIGN ---
    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        String baseStyle = "-fx-background-color: " + (active ? "#3498db" : "transparent") + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
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
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}