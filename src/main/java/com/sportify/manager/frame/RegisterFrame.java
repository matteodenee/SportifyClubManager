package com.sportify.manager.frame;

import com.sportify.manager.controllers.RegisterController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterFrame extends Application {

    private TextField idField = new TextField();
    private TextField nameField = new TextField();
    private TextField emailField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private PasswordField confirmPasswordField = new PasswordField();
    private ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
    private Label messageLabel = new Label();
    private RegisterController registerController;

    public RegisterFrame() {
        // Initialisation du contrôleur et lien avec cette vue
        this.registerController = new RegisterController();
        this.registerController.setRegisterFrame(this);
    }

    @Override
    public void start(Stage primaryStage) {
        // --- DESIGN DU CONTAINER PRINCIPAL ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;"); // Même bleu que le Login

        // --- TITRE ---
        Label titleLabel = new Label("INSCRIPTION");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        // --- FORMULAIRE ---
        VBox formBox = new VBox(12);
        formBox.setMaxWidth(300);
        formBox.setAlignment(Pos.CENTER);

        // Style des champs de saisie
        String inputStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-prompt-text-fill: #95a5a6; -fx-padding: 8; -fx-background-radius: 5;";

        idField.setPromptText("Identifiant (ex: M123)");
        idField.setStyle(inputStyle);

        nameField.setPromptText("Nom complet");
        nameField.setStyle(inputStyle);

        emailField.setPromptText("Email");
        emailField.setStyle(inputStyle);

        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle(inputStyle);

        confirmPasswordField.setPromptText("Confirmer mot de passe");
        confirmPasswordField.setStyle(inputStyle);

        // Configuration du sélecteur de rôle
        roleChoiceBox.setItems(FXCollections.observableArrayList("MEMBER", "COACH", "DIRECTOR"));
        roleChoiceBox.getSelectionModel().selectFirst();
        roleChoiceBox.setMaxWidth(Double.MAX_VALUE);
        roleChoiceBox.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");

        // --- BOUTONS ---
        Button btnRegister = new Button("CRÉER LE COMPTE");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand; -fx-background-radius: 5;");

        Button btnBack = new Button("RETOUR AU LOGIN");
        btnBack.setMaxWidth(Double.MAX_VALUE);
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-cursor: hand;");

        // Label pour les messages d'erreur ou succès
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        // Ajout des éléments au formulaire
        formBox.getChildren().addAll(
                createStyledLabel("Infos personnelles :"), nameField, emailField,
                createStyledLabel("Sécurité :"), idField, passwordField, confirmPasswordField,
                createStyledLabel("Rôle :"), roleChoiceBox,
                btnRegister, btnBack, messageLabel
        );

        root.getChildren().addAll(titleLabel, formBox);

        // --- ACTIONS ---
        btnRegister.setOnAction(e -> registerController.onClick());
        btnBack.setOnAction(e -> showLoginScreen());

        // Configuration de la scène
        Scene scene = new Scene(root, 400, 680);
        primaryStage.setTitle("Sportify - Inscription");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Gère la redirection vers l'écran de Login.
     */
    public void showLoginScreen() {
        Platform.runLater(() -> {
            try {
                // On récupère le Stage actuel
                Stage stage = (Stage) idField.getScene().getWindow();

                // On crée une nouvelle instance du LoginFrame et on la démarre
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.start(stage);
            } catch (Exception e) {
                System.err.println("Erreur lors de la redirection vers le Login : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Utilitaire pour créer des labels stylisés rapidement.
     */
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");
        return label;
    }

    // --- GETTERS POUR LE CONTROLLER ---
    public String getUserId() { return idField.getText(); }
    public String getUserName() { return nameField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getConfirmPassword() { return confirmPasswordField.getText(); }
    public String getRole() { return roleChoiceBox.getValue(); }

    public void showSuccess(String m) {
        messageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        messageLabel.setText(m);
    }

    public void showError(String m) {
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        messageLabel.setText(m);
    }
}