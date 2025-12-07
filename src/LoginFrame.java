package SportifyClubManager.src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginFrame extends Application {
    // fenetre javafx pour la page login
    private LoginController loginController;

    private TextField idField;
    private PasswordField pwdField;
    private Label messageLabel;

    public void setLoginController(LoginController controller) {
        // la vue recupere son controleur
        this.loginController = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        // si rien n'est injecte on fabrique un controleur par defaut
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

    public void showLoginSuccess(User user) {
        // message simple en cas de succes
        messageLabel.setText("Welcome " + user.getId() + "!");
    }

    public void showLoginError() {
        // message simple en cas d'erreur
        messageLabel.setText("Invalid credentials.");
    }

    public static void main(String[] args) {
        // entree standard pour lancer la fenetre
        launch(args);
    }
}
