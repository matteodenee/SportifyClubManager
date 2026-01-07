package com.sportify.manager.frame;

import com.sportify.manager.controllers.StatController;
import com.sportify.manager.services.Statistique;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class StatFrame {

    private StatController controller = new StatController();

    public void show(int teamId) {
        Stage stage = new Stage();

        // --- CONTAINER PRINCIPAL ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f4f7f6;");

        // --- HEADER ---
        VBox header = new VBox(5);
        Label title = new Label("Analyse de Performance");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subTitle = new Label("ID Équipe : " + teamId + " | Saison 2024");
        subTitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        header.getChildren().addAll(title, subTitle);

        // --- TABLEAU STYLISÉ ---
        TableView<Statistique> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        TableColumn<Statistique, String> colType = new TableColumn<>("Indicateur");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<Statistique, Double> colVal = new TableColumn<>("Total");
        colVal.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        colVal.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        table.getColumns().addAll(colType, colVal);

        // --- CHARGEMENT DES DONNÉES ---
        List<Statistique> stats = controller.getStatsForTeam(teamId, "Saison 2024");
        table.setItems(FXCollections.observableArrayList(stats));

        // --- PIED DE PAGE ---
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnClose = new Button("Fermer");
        btnClose.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        btnClose.setOnAction(e -> stage.close());
        footer.getChildren().add(btnClose);

        // --- ASSEMBLAGE ---
        root.getChildren().addAll(header, table, footer);
        VBox.setVgrow(table, Priority.ALWAYS); // Le tableau prend toute la place disponible

        Scene scene = new Scene(root, 450, 550);
        stage.setTitle("Sportify - Statistiques");

        // Empêche d'ouvrir 50 fois la même fenêtre si on clique trop
        stage.setResizable(false);
        stage.show();
    }
}