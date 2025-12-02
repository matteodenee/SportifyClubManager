package SportifyClubManager.src;


import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginFrame extends Stage {

    private TextField userIdField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label messageLabel;

    public LoginFrame() {

        // Champs
        userIdField = new TextField();
        userIdField.setPromptText("User ID");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        loginButton = new Button("Login");

        messageLabel = new Label();

        VBox box = new VBox(10, userIdField, passwordField, loginButton, messageLabel);
        box.setStyle("-fx-padding: 30;");

        Scene scene = new Scene(box, 300, 200);

        this.setScene(scene);
        this.setTitle("Login");
    }

    // ========= GETTERS POUR LE CONTROLLER =========

    public TextField getUserIdField() {
        return userIdField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    // Méthode utilitaire
    public void showMessage(String msg) {
        messageLabel.setText(msg);
    }
}
