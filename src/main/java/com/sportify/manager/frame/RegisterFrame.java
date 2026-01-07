package com.sportify.manager.frame;

import com.sportify.manager.controllers.RegisterController;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Java FX boundary class for user registration.
public class RegisterFrame {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private Label messageLabel;

    private RegisterController registerController;
    private LoginFrame loginFrame;
    private Stage stage;

    /**
     * Injects the controller and wires it with this boundary.
     * @param controller register controller instance
     */
    public void setRegisterController(RegisterController controller) {
        this.registerController = controller;
        if (controller != null) {
            controller.setRegisterFrame(this);
        }
    }

    /**
     * Sets the login frame used to navigate back after a successful registration.
     * @param loginFrame login boundary
     */
    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    /**
     * Keeps a reference to the stage so we can swap scenes.
     * @param stage primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Default wiring if nothing injected
        if (registerController == null) {
            setRegisterController(new RegisterController());
        } else {
            registerController.setRegisterFrame(this);
        }

        if (roleChoiceBox != null) {
            // Admin n'est pas sélectionnable via l'UI (créé ailleurs par défaut)
            roleChoiceBox.setItems(FXCollections.observableArrayList("MEMBER", "COACH", "DIRECTOR"));
            roleChoiceBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void onRegister() {
        if (registerController != null) {
            registerController.onClick();
        }
    }

    @FXML
    private void onBack() {
        showLoginScreen();
    }

    public String getUserId() {
        return idField != null ? idField.getText() : "";
    }

    public String getUserName() {
        return nameField != null ? nameField.getText() : "";
    }

    public String getPassword() {
        return passwordField != null ? passwordField.getText() : "";
    }

    public String getConfirmPassword() {
        return confirmPasswordField != null ? confirmPasswordField.getText() : "";
    }

    public String getEmail() {
        return emailField != null ? emailField.getText() : "";
    }

    public String getRole() {
        return roleChoiceBox != null && roleChoiceBox.getValue() != null ? roleChoiceBox.getValue() : "";
    }

    public void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(message);
        }
    }

    public void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(message);
        }
    }

    
    // Returns to the login screen using the shared stage.
    public void showLoginScreen() {
        if (loginFrame != null && stage != null) {
            try {
                loginFrame.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
