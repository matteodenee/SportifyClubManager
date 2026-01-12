package com.sportify.manager.frame;

import com.sportify.manager.controllers.SmallEventController;
import com.sportify.manager.controllers.StatController;
import com.sportify.manager.controllers.TeamController;
import com.sportify.manager.facade.MatchFacade;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchStatus;
import com.sportify.manager.services.SmallEvent;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class StatFrame {

    private PieChart pieChart;
    private VBox kpiContainer;
    private TableView<Match> matchTable;
    private ComboBox<Team> teamCombo;
    private ListView<String> matchEventList;
    private Label matchEventSummary;
    private int currentTeamId;
    private int currentClubId;
    private final MatchFacade matchFacade = MatchFacade.getInstance();
    private final StatController statController = new StatController();
    private final SmallEventController smallEventController = new SmallEventController();
    private final TeamController teamController = TeamController.getInstance();
    private final Map<Integer, String> teamNameCache = new HashMap<>();
    private final Map<String, String> playerNameCache = new HashMap<>();

    public void show(List<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return;
        }
        Team initial = teams.get(0);
        this.currentTeamId = initial.getId();
        this.currentClubId = initial.getClubId();
        Stage stage = new Stage();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f4f7f6;");

        // --- TOP : FILTRES (Use Case 7) ---
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(0, 0, 20, 0));

        Label teamLabel = new Label("Équipe :");
        teamCombo = new ComboBox<>(FXCollections.observableArrayList(teams));
        teamCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        teamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        Label filterLabel = new Label("Période :");
        ComboBox<String> periodCombo = new ComboBox<>(FXCollections.observableArrayList("Global"));
        periodCombo.getSelectionModel().selectFirst();
        periodCombo.setOnAction(e -> updateDashboard(periodCombo.getValue()));
        teamCombo.getSelectionModel().select(initial);
        teamCombo.setOnAction(e -> {
            Team selected = teamCombo.getValue();
            if (selected != null) {
                currentTeamId = selected.getId();
                currentClubId = selected.getClubId();
                updateDashboard(periodCombo.getValue());
            }
        });

        filterBar.getChildren().addAll(teamLabel, teamCombo, filterLabel, periodCombo);
        mainLayout.setTop(filterBar);

        pieChart = new PieChart();
        pieChart.setTitle("Résultats (V/N/D)");
        pieChart.setLegendVisible(true);

        matchTable = new TableView<>();
        setupMatchTable();
        matchTable.setPrefHeight(180);
        matchTable.setMaxHeight(220);
        ScrollPane matchScroll = new ScrollPane(matchTable);
        matchScroll.setFitToWidth(true);
        matchScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        matchScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        matchTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadMatchEvents(newVal));

        matchEventSummary = new Label();
        matchEventSummary.setStyle("-fx-text-fill: #7f8c8d;");
        matchEventList = new ListView<>();
        matchEventList.setPrefHeight(220);
        matchEventList.setPlaceholder(new Label("Sélectionnez un match"));

        VBox centerBox = new VBox(12, pieChart, new Label("Matchs terminés"), matchScroll, new Label("Évènements du match"), matchEventSummary, matchEventList);
        VBox.setVgrow(matchTable, Priority.SOMETIMES);
        VBox.setVgrow(matchEventList, Priority.ALWAYS);
        ScrollPane centerScroll = new ScrollPane(centerBox);
        centerScroll.setFitToWidth(true);
        centerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainLayout.setCenter(centerScroll);

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
        updateDashboard(periodCombo.getValue());

        Scene scene = new Scene(mainLayout, 1100, 760);
        stage.setTitle("Tableau de Bord Statistique - Sportify");
        stage.setScene(scene);
        stage.show();
    }

    private void updateDashboard(String period) {
        String effectivePeriod = (period == null || period.isBlank()) ? "Global" : period;
        List<Match> matches = loadMatches(effectivePeriod);
        Map<String, Integer> results = computeResults(matches);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Victoires", results.getOrDefault("WIN", 0)),
                new PieChart.Data("Nuls", results.getOrDefault("DRAW", 0)),
                new PieChart.Data("Défaites", results.getOrDefault("LOSS", 0))
        );
        pieChart.setData(pieData);

        kpiContainer.getChildren().clear();
        Map<String, Double> kpis = computeKpis(matches, results);
        Map<String, Integer> smallEvents = statController.getTeamDistribution(currentTeamId, toSmallEventPeriod(effectivePeriod));
        for (Map.Entry<String, Double> entry : kpis.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();
            String formatted = label.contains("Rate") ? String.format("%.1f%%", value) : String.format("%.0f", value);
            kpiContainer.getChildren().add(createKPICard(label, formatted));
        }
        for (Map.Entry<String, Integer> entry : smallEvents.entrySet()) {
            String type = entry.getKey();
            if (isMatchResultEvent(type)) {
                continue;
            }
            kpiContainer.getChildren().add(createKPICard("Evt " + type, String.valueOf(entry.getValue())));
        }

        matchTable.setItems(FXCollections.observableArrayList(matches));
        matchTable.getSelectionModel().clearSelection();
        matchEventList.setItems(FXCollections.observableArrayList());
        matchEventSummary.setText("");
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

    private void setupMatchTable() {
        TableColumn<Match, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(formatDate(cell.getValue().getDateTime())));

        TableColumn<Match, String> opponentCol = new TableColumn<>("Adversaire");
        opponentCol.setCellValueFactory(cell -> new SimpleStringProperty(getOpponent(cell.getValue())));

        TableColumn<Match, String> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(cell -> new SimpleStringProperty(getScoreForClub(cell.getValue())));

        TableColumn<Match, String> locationCol = new TableColumn<>("Lieu");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Match, String> resultCol = new TableColumn<>("Résultat");
        resultCol.setCellValueFactory(cell -> new SimpleStringProperty(getResultForClub(cell.getValue())));

        matchTable.getColumns().addAll(dateCol, opponentCol, scoreCol, locationCol, resultCol);
        matchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        matchTable.setPlaceholder(new Label("Aucun match terminé"));
    }

    private List<Match> loadMatches(String period) {
        List<Match> matches = new ArrayList<>();
        try {
            matches = matchFacade.getMatchesByClub(currentClubId);
        } catch (Exception e) {
            return List.of();
        }
        List<Match> filtered = new ArrayList<>();
        for (Match m : matches) {
            if (m == null || m.getStatus() != MatchStatus.FINISHED) {
                continue;
            }
            if (m.getHomeTeamId() != currentTeamId && m.getAwayTeamId() != currentTeamId) {
                continue;
            }
            if (!isInPeriod(m.getDateTime(), period)) {
                continue;
            }
            filtered.add(m);
        }
        return filtered;
    }

    private Map<String, Integer> computeResults(List<Match> matches) {
        Map<String, Integer> results = new HashMap<>();
        for (Match m : matches) {
            String result = getResultForClub(m);
            switch (result) {
                case "Victoire" -> results.merge("WIN", 1, Integer::sum);
                case "Nul" -> results.merge("DRAW", 1, Integer::sum);
                case "Défaite" -> results.merge("LOSS", 1, Integer::sum);
                default -> { }
            }
        }
        return results;
    }

    private Map<String, Double> computeKpis(List<Match> matches, Map<String, Integer> results) {
        Map<String, Double> kpis = new HashMap<>();
        int played = matches.size();
        int wins = results.getOrDefault("WIN", 0);
        int draws = results.getOrDefault("DRAW", 0);
        int losses = results.getOrDefault("LOSS", 0);

        int goalsFor = 0;
        int goalsAgainst = 0;
        for (Match m : matches) {
            Integer home = m.getHomeScore();
            Integer away = m.getAwayScore();
            if (home == null || away == null) {
                continue;
            }
            if (m.getHomeTeamId() == currentTeamId) {
                goalsFor += home;
                goalsAgainst += away;
            } else if (m.getAwayTeamId() == currentTeamId) {
                goalsFor += away;
                goalsAgainst += home;
            }
        }

        kpis.put("Matchs joués", (double) played);
        kpis.put("Victoires", (double) wins);
        kpis.put("Nuls", (double) draws);
        kpis.put("Défaites", (double) losses);
        kpis.put("Buts pour", (double) goalsFor);
        kpis.put("Buts contre", (double) goalsAgainst);
        kpis.put("Diff. buts", (double) (goalsFor - goalsAgainst));
        if (played > 0) {
            kpis.put("WinRate", (double) wins / played * 100);
        }
        return kpis;
    }

    private boolean isInPeriod(LocalDateTime dateTime, String period) {
        if (dateTime == null) {
            return false;
        }
        if (period == null || period.isBlank()) {
            return true;
        }
        if ("Global".equalsIgnoreCase(period)) {
            return true;
        }
        if (period.startsWith("Saison ")) {
            String year = period.substring("Saison ".length()).trim();
            try {
                int y = Integer.parseInt(year);
                return dateTime.getYear() == y;
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }

    private String getResultForClub(Match m) {
        if (m.getHomeScore() == null || m.getAwayScore() == null) {
            return "-";
        }
        int gf = m.getHomeTeamId() == currentTeamId ? m.getHomeScore() : m.getAwayScore();
        int ga = m.getHomeTeamId() == currentTeamId ? m.getAwayScore() : m.getHomeScore();
        if (gf > ga) return "Victoire";
        if (gf < ga) return "Défaite";
        return "Nul";
    }

    private String getScoreForClub(Match m) {
        if (m.getHomeScore() == null || m.getAwayScore() == null) {
            return "-";
        }
        if (m.getHomeTeamId() == currentTeamId) {
            return m.getHomeScore() + " - " + m.getAwayScore();
        }
        return m.getAwayScore() + " - " + m.getHomeScore();
    }

    private String getOpponent(Match m) {
        int opponentId = (m.getHomeTeamId() == currentTeamId) ? m.getAwayTeamId() : m.getHomeTeamId();
        return getTeamName(opponentId);
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.toLocalDate().toString();
    }

    private String getTeamName(int teamId) {
        if (teamNameCache.containsKey(teamId)) {
            return teamNameCache.get(teamId);
        }
        Team team = teamController.handleGetTeamById(teamId);
        String name = team == null ? ("Équipe " + teamId) : team.getNom();
        teamNameCache.put(teamId, name);
        return name;
    }

    private String toSmallEventPeriod(String period) {
        if (period != null && period.startsWith("Saison ")) {
            return period;
        }
        return "Saison " + LocalDate.now().getYear();
    }

    private boolean isMatchResultEvent(String type) {
        if (type == null) {
            return false;
        }
        String t = type.toUpperCase();
        return t.equals("MATCH") || t.equals("VICTOIRE") || t.equals("DEFAITE") || t.equals("NUL");
    }

    private void loadMatchEvents(Match match) {
        if (match == null) {
            matchEventList.setItems(FXCollections.observableArrayList());
            matchEventSummary.setText("");
            return;
        }
        List<SmallEvent> events = smallEventController.handleGetByMatch(match.getId());
        List<String> rows = new ArrayList<>();
        int homeGoals = 0;
        int awayGoals = 0;
        for (SmallEvent e : events) {
            String type = e.getType();
            if ("GOAL".equalsIgnoreCase(type) || "BUT".equalsIgnoreCase(type)) {
                if (e.getTeamId() == match.getHomeTeamId()) {
                    homeGoals++;
                } else if (e.getTeamId() == match.getAwayTeamId()) {
                    awayGoals++;
                }
            }
            String teamName = getTeamName(e.getTeamId());
            String player = e.getPlayerId() == null ? "" : (" | " + getPlayerName(e.getPlayerId()));
            String desc = e.getDescription() == null ? "" : (" - " + e.getDescription());
            rows.add(teamName + " | " + type + desc + player);
        }
        matchEventList.setItems(FXCollections.observableArrayList(rows));
        String homeScore = match.getHomeScore() == null ? "-" : match.getHomeScore().toString();
        String awayScore = match.getAwayScore() == null ? "-" : match.getAwayScore().toString();
        matchEventSummary.setText("Score: " + homeScore + " - " + awayScore + " | Buts évènements: " + homeGoals + " - " + awayGoals);
    }

    private String getPlayerName(String playerId) {
        if (playerNameCache.containsKey(playerId)) {
            return playerNameCache.get(playerId);
        }
        User user = PostgresUserDAO.getInstance().getUserById(playerId);
        String name = user == null ? playerId : user.getName();
        playerNameCache.put(playerId, name);
        return name;
    }
}
