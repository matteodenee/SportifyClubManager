package licenceManagement.UI.frames;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import licenceManagement.Enum.TypeLicence;
import licenceManagement.UI.controllers.LicenceController;
import TypeSportManagement.TypeSport;

public class LicenceFrame extends Application {

    // Vue JavaFX pour la demande de licence
    private LicenceController licenceController;

    private ComboBox<TypeSport> sportBox;
    private ComboBox<TypeLicence> typeLicenceBox;
    private Label messageLabel;

    public void setLicenceController(LicenceController controller) {
        this.licenceController = controller;
    }

    @Override
    public void start(Stage primaryStage) {

        // Injection par défaut si rien n'est fourni
        if (licenceController == null) {
            licenceController = new LicenceController();
        }
        licenceController.setLicenceFrame(this);

        sportBox = new ComboBox<>();
        typeLicenceBox = new ComboBox<>();
        messageLabel = new Label();

        // Exemples FAKE (à remplacer par DAO plus tard)
        sportBox.getItems().addAll(
                new TypeSport(1, "Football"),
                new TypeSport(2, "Basket"),
                new TypeSport(3, "Yoga")
        );

        typeLicenceBox.getItems().addAll(TypeLicence.values());



        Button demanderButton = new Button("Demander licence");
        demanderButton.setOnAction(event -> {
            TypeSport sport = sportBox.getValue();
            TypeLicence type = typeLicenceBox.getValue();
            licenceController.onDemandeLicence(sport, type);
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Sport :"), 0, 0);
        grid.add(sportBox, 1, 0);

        grid.add(new Label("Type de licence :"), 0, 1);
        grid.add(typeLicenceBox, 1, 1);

        grid.add(demanderButton, 1, 2);
        grid.add(messageLabel, 1, 3);

        Scene scene = new Scene(grid, 400, 220);
        primaryStage.setTitle("Sportify Club Manager - Licence");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // =========================
    // Méthodes appelées par le controller
    // =========================

    public void showSuccess() {
        messageLabel.setText("Licence demandée avec succès.");
    }

    public void showError(String message) {
        messageLabel.setText(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
