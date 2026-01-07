package com.sportify.manager.frame;

import com.sportify.manager.controllers.StatController;
import com.sportify.manager.services.Statistique;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class StatFrame {

    private StatController controller = new StatController();

    /**
     * La méthode show accepte maintenant l'ID de l'équipe
     */
    public void show(int teamId) {
        Stage stage = new Stage();
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("Statistiques de l'équipe (ID: " + teamId + ")");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Statistique> table = new TableView<>();

        // Configuration des colonnes
        TableColumn<Statistique, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Statistique, Double> colVal = new TableColumn<>("Valeur");
        colVal.setCellValueFactory(new PropertyValueFactory<>("valeur"));

        table.getColumns().addAll(colType, colVal);

        // Chargement des données DYNAMIQUE avec l'ID reçu
        List<Statistique> stats = controller.getStatsForTeam(teamId, "Saison 2024");
        table.setItems(FXCollections.observableArrayList(stats));

        layout.getChildren().addAll(title, table);

        Scene scene = new Scene(layout, 400, 500);
        stage.setTitle("Sportify - Analyse Performance");
        stage.setScene(scene);
        stage.show();
    }
}