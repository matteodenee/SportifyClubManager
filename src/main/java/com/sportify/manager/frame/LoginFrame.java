package com.sportify.manager.frame;

import com.sportify.manager.controllers.LoginController;
import com.sportify.manager.services.User;
import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.dao.PostgresUserDAO;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.sql.Connection;

public class LoginFrame extends Application {

    private LoginController loginController;
    private TextField idField;
    private PasswordField pwdField;
    private Label messageLabel;

    public void setLoginController(LoginController controller) {
        this.loginController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        if (loginController == null) {
            loginController = new LoginController();
        }
        loginController.setLoginFrame(this);

        // --- DESIGN DU CONTAINER PRINCIPAL ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #2c3e50;"); // Fond sombre comme la sidebar

        // --- LOGO OU TITRE ---
        Label titleLabel = new Label("SPORTIFY");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");

        Label subTitle = new Label("Club Management System");
        subTitle.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px;");

        // --- FORMULAIRE ---
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(300);

        idField = new TextField();
        idField.setPromptText("Identifiant");
        idField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-prompt-text-fill: #95a5a6; -fx-padding: 10; -fx-background-radius: 5;");

        pwdField = new PasswordField();
        pwdField.setPromptText("Mot de passe");
        pwdField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-prompt-text-fill: #95a5a6; -fx-padding: 10; -fx-background-radius: 5;");

        Button loginButton = new Button("SE CONNECTER");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5; -fx-cursor: hand;");

        // Effet de survol pour le bouton
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;"));

        loginButton.setOnAction(event -> {
            loginController.onClick(idField.getText(), pwdField.getText());
        });

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        formBox.getChildren().addAll(idField, pwdField, loginButton, messageLabel);

        root.getChildren().addAll(titleLabel, subTitle, formBox);

        Scene scene = new Scene(root, 400, 450);
        primaryStage.setTitle("Sportify - Login");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- LOGIQUE DE REDIRECTION ---
    public void showLoginSuccess(User user) {
        String role = user.getRole().toUpperCase();
        switch (role) {
            case "ADMIN": openClubManagementFrame(); break;
            case "DIRECTOR": openDirectorDashboard(user); break;
            case "MEMBER": openMemberDashboard(user); break;
            case "COACH": openCoachDashboard(user); break;
            default: messageLabel.setText("Rôle inconnu : " + role);
        }
    }

    public void showLoginError() {
        messageLabel.setText("Identifiants incorrects.");
    }

    // --- OUVERTURE DES DASHBOARDS ---
    public void openCoachDashboard(User user) {
        try {
            CoachDashboardFrame coachFrame = new CoachDashboardFrame(user);
            coachFrame.start(new Stage());
            closeCurrentStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void openClubManagementFrame() {
        try {
            Connection connection = PostgresUserDAO.getConnection();
            ClubController clubController = new ClubController(connection);
            ClubManagementFrame clubManagementFrame = new ClubManagementFrame();
            clubManagementFrame.setClubController(clubController);
            clubManagementFrame.start(new Stage());
            closeCurrentStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void openDirectorDashboard(User user) {
        try {
            Connection connection = PostgresUserDAO.getConnection();
            ClubController clubController = new ClubController(connection);
            DirectorDashboardFrame directorFrame = new DirectorDashboardFrame(user);
            directorFrame.setClubController(clubController);
            directorFrame.start(new Stage());
            closeCurrentStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void openMemberDashboard(User user) {
        try {
            Connection connection = PostgresUserDAO.getConnection();
            ClubController clubController = new ClubController(connection);
            MemberDashboardFrame memberFrame = new MemberDashboardFrame(user);
            memberFrame.setClubController(clubController);
            memberFrame.start(new Stage());
            closeCurrentStage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void closeCurrentStage() {
        if (idField.getScene() != null) {
            ((Stage) idField.getScene().getWindow()).close();
        }
    }

    public static void main(String[] args) { launch(args); }
}