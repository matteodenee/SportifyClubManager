package com.sportify.manager.frame;

import com.sportify.manager.controllers.StatController;
import com.sportify.manager.services.Statistique;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;

public class StatFrame {

    private StatController controller = new StatController();
    private PieChart pieChart;
    private VBox kpiContainer;
    private int currentTeamId;

    public void show(int teamId) {
        this.currentTeamId = teamId;
        Stage stage = new Stage();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f4f7f6;");

        // --- TOP : FILTRES (Use Case 7) ---
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(0, 0, 20, 0));

        Label filterLabel = new Label("Période :");
        ComboBox<String> periodCombo = new ComboBox<>(FXCollections.observableArrayList("Saison 2024", "Dernier Mois", "Global"));
        periodCombo.setValue("Saison 2024");

        periodCombo.setOnAction(e -> updateDashboard(periodCombo.getValue()));

        filterBar.getChildren().addAll(filterLabel, periodCombo);
        mainLayout.setTop(filterBar);

        // --- CENTER : GRAPHIQUE (Use Case 2) ---
        pieChart = new PieChart();
        pieChart.setTitle("Répartition des Actions");
        pieChart.setLegendVisible(true);

        mainLayout.setCenter(pieChart);

        // --- RIGHT : KPI (Ratios du flux 9.2.1) ---
        kpiContainer = new VBox(15);
        kpiContainer.setPadding(new Insets(0, 0, 0, 20));
        kpiContainer.setAlignment(Pos.TOP_CENTER);

        mainLayout.setRight(kpiContainer);

        // --- BOTTOM : ACTIONS ---
        HBox footer = new HBox();
        footer.setPadding(new Insets(20, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnClose = new Button("Fermer l'analyse");
        btnClose.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        btnClose.setOnAction(e -> stage.close());
        footer.getChildren().add(btnClose);
        mainLayout.setBottom(footer);

        // Chargement initial
        updateDashboard("Saison 2024");

        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setTitle("Tableau de Bord Statistique - Sportify");
        stage.setScene(scene);
        stage.show();
    }

    private void updateDashboard(String period) {
        // 1. Mise à jour du PieChart (Distribution)
        Map<String, Integer> distribution = controller.getTeamDistribution(currentTeamId, period);
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        distribution.forEach((type, total) -> pieData.add(new PieChart.Data(type, total)));
        pieChart.setData(pieData);

        // 2. Mise à jour des KPI (Ratios calculés par le controller/manager)
        kpiContainer.getChildren().clear();
        Map<String, Double> ratios = controller.getPerformanceRatios(currentTeamId, period);

        ratios.forEach((label, value) -> {
            VBox card = createKPICard(label, String.format("%.2f", value) + (label.contains("Rate") ? "%" : ""));
            kpiContainer.getChildren().add(card);
        });
    }

    private VBox createKPICard(String title, String value) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }
}