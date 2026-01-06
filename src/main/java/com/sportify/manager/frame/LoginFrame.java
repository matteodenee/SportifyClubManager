package com.sportify.manager.frame;

import com.sportify.manager.controllers.LoginController;
import com.sportify.manager.services.User;
import com.sportify.manager.controllers.ClubController;
import com.sportify.manager.dao.PostgresUserDAO;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

        idField = new TextField();
        pwdField = new PasswordField();
        messageLabel = new Label();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            String id = idField.getText();
            String pwd = pwdField.getText();
            loginController.onClick(id, pwd);
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("User id:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(pwdField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(messageLabel, 1, 3);

        Scene scene = new Scene(grid, 350, 180);
        primaryStage.setTitle("Sportify Club Manager - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- LOGIQUE DE REDIRECTION PAR RÔLE ---
    public void showLoginSuccess(User user) {
        // On s'assure que le rôle est traité de manière insensible à la casse
        String role = user.getRole().toUpperCase();

        switch (role) {
            case "ADMIN":
                openClubManagementFrame();
                break;

            case "DIRECTOR":
                // Maintenant, le directeur va vers sa propre interface
                openDirectorDashboard(user);
                break;

            case "MEMBER":
                openMemberDashboard(user);
                break;

            case "COACH":
                messageLabel.setText("Coach space not yet implemented.");
                break;

            default:
                messageLabel.setText("Unknown role: " + role);
        }
    }

    public void showLoginError() {
        messageLabel.setText("Invalid credentials.");
    }

    // --- FENÊTRES DE DESTINATION ---

    /**
     * Fenêtre pour ADMIN (Gestion de la structure des clubs)
     */
    public void openClubManagementFrame() {
        try {
            Connection connection = PostgresUserDAO.getConnection();
            ClubController clubController = new ClubController(connection);

            ClubManagementFrame clubManagementFrame = new ClubManagementFrame();
            clubManagementFrame.setClubController(clubController);

            Stage clubStage = new Stage();
            clubManagementFrame.start(clubStage);

            closeCurrentStage();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Admin UI error: " + e.getMessage());
        }
    }

    /**
     * Fenêtre pour DIRECTOR (Gestion des demandes d'adhésion)
     */
    public void openDirectorDashboard(User user) {
        try {
            Connection connection = PostgresUserDAO.getConnection();
            ClubController clubController = new ClubController(connection);

            DirectorDashboardFrame directorFrame = new DirectorDashboardFrame(user);
            directorFrame.setClubController(clubController);

            Stage directorStage = new Stage();
            directorFrame.start(directorStage);

            closeCurrentStage();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Director UI error: " + e.getMessage());
        }
    }

    /**
     * Fenêtre pour MEMBER (Consultation et demandes)
     */
    public void openMemberDashboard(User user) {
        try {
            Connection connection = PostgresUserDAO.getConnection();
            ClubController clubController = new ClubController(connection);

            MemberDashboardFrame memberFrame = new MemberDashboardFrame(user);
            memberFrame.setClubController(clubController);

            Stage memberStage = new Stage();
            memberFrame.start(memberStage);

            closeCurrentStage();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Member UI error: " + e.getMessage());
        }
    }

    /**
     * Utilitaire pour fermer la fenêtre de login
     */
    private void closeCurrentStage() {
        if (idField.getScene() != null && idField.getScene().getWindow() != null) {
            ((Stage) idField.getScene().getWindow()).close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}