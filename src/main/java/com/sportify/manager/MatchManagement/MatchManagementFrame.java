package com.sportify.manager.MatchManagement;

import com.sportify.manager.match.controllers.MatchController;
import com.sportify.manager.match.model.Match;
import com.sportify.manager.match.model.MatchStatus;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class MatchManagementFrame extends Application {

    private MatchController controller;

    private TextField idField;
    private TextField typeSportIdField;
    private TextField homeTeamIdField;
    private TextField awayTeamIdField;
    private TextField dateTimeField; // format: 2026-01-08T18:30
    private TextField locationField;
    private TextField refereeField;
    private TextField deadlineField; // format: 2026-01-07T18:00

    private TableView<Match> table;

    public void setController(MatchController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage stage) {
        if (controller == null) controller = MatchController.getInstance();

        idField = new TextField(); idField.setPromptText("id (pour update)");
        typeSportIdField = new TextField(); typeSportIdField.setPromptText("typeSportId");
        homeTeamIdField = new TextField(); homeTeamIdField.setPromptText("homeTeamId");
        awayTeamIdField = new TextField(); awayTeamIdField.setPromptText("awayTeamId");
        dateTimeField = new TextField(); dateTimeField.setPromptText("dateTime ex: 2026-01-08T18:30");
        locationField = new TextField(); locationField.setPromptText("location");
        refereeField = new TextField(); refereeField.setPromptText("referee (optionnel)");
        deadlineField = new TextField(); deadlineField.setPromptText("deadline ex: 2026-01-08T16:00 (optionnel)");

        Button createBtn = new Button("Créer Match");
        Button updateBtn = new Button("Modifier Match");
        Button refreshBtn = new Button("Refresh Liste");

        createBtn.setOnAction(e -> onCreate());
        updateBtn.setOnAction(e -> onUpdate());
        refreshBtn.setOnAction(e -> loadMatches());

        HBox buttons = new HBox(10, createBtn, updateBtn, refreshBtn);

        table = new TableView<>();
        TableColumn<Match, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Match, Integer> sportCol = new TableColumn<>("SportId");
        sportCol.setCellValueFactory(new PropertyValueFactory<>("typeSportId"));

        TableColumn<Match, Integer> homeCol = new TableColumn<>("Home");
        homeCol.setCellValueFactory(new PropertyValueFactory<>("homeTeamId"));

        TableColumn<Match, Integer> awayCol = new TableColumn<>("Away");
        awayCol.setCellValueFactory(new PropertyValueFactory<>("awayTeamId"));

        TableColumn<Match, LocalDateTime> dtCol = new TableColumn<>("DateTime");
        dtCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));

        table.getColumns().addAll(idCol, sportCol, homeCol, awayCol, dtCol);
        table.setPrefHeight(260);

        VBox root = new VBox(10,
                new Label("Match Management"),
                idField, typeSportIdField, homeTeamIdField, awayTeamIdField,
                dateTimeField, locationField, refereeField, deadlineField,
                buttons,
                table
        );
        root.setPadding(new Insets(20));

        loadMatches();

        stage.setTitle("Sportify - Match Management");
        stage.setScene(new Scene(root, 600, 650));
        stage.show();
    }

    private void onCreate() {
        try {
            Match m = buildMatch(false);
            Match created = controller.handleCreateMatch(m);
            if (created == null) showAlert("Erreur création match");
            else {
                showAlert("Match créé id=" + created.getId());
                loadMatches();
            }
        } catch (Exception ex) {
            showAlert("Entrées invalides");
        }
    }

    private void onUpdate() {
        try {
            Match m = buildMatch(true);
            boolean ok = controller.handleUpdateMatch(m);
            showAlert(ok ? "Match modifié" : "Erreur modification");
            if (ok) loadMatches();
        } catch (Exception ex) {
            showAlert("Entrées invalides");
        }
    }

    private Match buildMatch(boolean requireId) {
        Integer id = null;
        if (requireId) id = Integer.parseInt(idField.getText().trim());

        int typeSportId = Integer.parseInt(typeSportIdField.getText().trim());
        int home = Integer.parseInt(homeTeamIdField.getText().trim());
        int away = Integer.parseInt(awayTeamIdField.getText().trim());
        LocalDateTime dt = LocalDateTime.parse(dateTimeField.getText().trim());
        String location = locationField.getText().trim();
        String referee = refereeField.getText().trim();

        LocalDateTime deadline = null;
        String dlText = deadlineField.getText();
        if (dlText != null && !dlText.trim().isEmpty()) deadline = LocalDateTime.parse(dlText.trim());

        return new Match(
                id,
                typeSportId,
                home,
                away,
                dt,
                location,
                referee.isEmpty() ? null : referee,
                deadline,
                MatchStatus.SCHEDULED,
                null, null
        );
    }

    private void loadMatches() {
        List<Match> matches = controller.handleGetAllMatches();
        if (matches != null) table.setItems(FXCollections.observableArrayList(matches));
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
