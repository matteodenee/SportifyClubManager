package com.sportify.manager.frame;

import com.sportify.manager.controllers.TrainingController;
import com.sportify.manager.services.Entrainement;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrainingFrame {

    private final TrainingController trainingController = new TrainingController();

    private User currentUser;

    private DatePicker datePicker;
    private TextField timeField;
    private TextField locationField;
    private TextField activityField;
    private TextField clubIdField;
    private DatePicker fromDatePicker;

    private TableView<Entrainement> trainingTable;
    private TextField participationUserIdField;
    private ChoiceBox<ParticipationStatus> statusChoiceBox;
    private ListView<String> participationList;
    private Label messageLabel;

    public TrainingFrame() {
        trainingController.setTrainingFrame(this);
    }

    public static void open(User user, int clubId) {
        new TrainingFrame().show(user, clubId);
    }

    public void show(User user, int clubId) {
        this.currentUser = user;
        Stage stage = new Stage();
        Scene scene = new Scene(buildRoot(clubId), 900, 650);
        stage.setTitle("Sportify - Training Management");
        stage.setScene(scene);
        stage.show();
        trainingController.onRefresh();
    }

    private VBox buildRoot(int clubId) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label title = new Label("Training Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);

        datePicker = new DatePicker();
        timeField = new TextField();
        timeField.setPromptText("18:30");
        locationField = new TextField();
        activityField = new TextField();
        clubIdField = new TextField();

        form.add(new Label("Date:"), 0, 0);
        form.add(datePicker, 1, 0);
        form.add(new Label("Heure (HH:mm):"), 2, 0);
        form.add(timeField, 3, 0);

        form.add(new Label("Lieu:"), 0, 1);
        form.add(locationField, 1, 1);
        form.add(new Label("Activite:"), 2, 1);
        form.add(activityField, 3, 1);

        form.add(new Label("Club ID:"), 0, 2);
        form.add(clubIdField, 1, 2);

        if (clubId > 0) {
            clubIdField.setText(String.valueOf(clubId));
            clubIdField.setDisable(true);
        }

        Button createButton = new Button("Planifier");
        createButton.setOnAction(e -> trainingController.onCreate());
        HBox createRow = new HBox(10, createButton);
        createRow.setAlignment(Pos.CENTER_LEFT);

        Label upcomingLabel = new Label("Entrainements a venir");
        upcomingLabel.setStyle("-fx-font-weight: bold;");

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.getChildren().add(new Label("A partir du:"));
        fromDatePicker = new DatePicker(LocalDate.now());
        Button refreshButton = new Button("Rafraichir");
        refreshButton.setOnAction(e -> trainingController.onRefresh());
        filterRow.getChildren().addAll(fromDatePicker, refreshButton);

        trainingTable = new TableView<>();
        trainingTable.setPrefHeight(220);
        TableColumn<Entrainement, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Entrainement, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Entrainement, LocalTime> timeCol = new TableColumn<>("Heure");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("heure"));
        TableColumn<Entrainement, String> locationCol = new TableColumn<>("Lieu");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        TableColumn<Entrainement, String> activityCol = new TableColumn<>("Activite");
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activite"));
        TableColumn<Entrainement, Integer> clubCol = new TableColumn<>("Club");
        clubCol.setCellValueFactory(new PropertyValueFactory<>("clubId"));
        trainingTable.getColumns().addAll(idCol, dateCol, timeCol, locationCol, activityCol, clubCol);
        trainingTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> trainingController.onLoadParticipation()
        );

        Label participationLabel = new Label("Participation");
        participationLabel.setStyle("-fx-font-weight: bold;");

        participationUserIdField = new TextField();
        if (currentUser != null) {
            participationUserIdField.setPromptText("ex: " + currentUser.getId());
        } else {
            participationUserIdField.setPromptText("User ID");
        }

        statusChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(ParticipationStatus.values()));
        statusChoiceBox.getSelectionModel().select(ParticipationStatus.PENDING);

        Button markButton = new Button("Marquer");
        markButton.setOnAction(e -> trainingController.onMarkParticipation());

        HBox participationRow = new HBox(10, participationUserIdField, statusChoiceBox, markButton);
        participationRow.setAlignment(Pos.CENTER_LEFT);

        participationList = new ListView<>();
        participationList.setPrefHeight(120);

        messageLabel = new Label();

        root.getChildren().addAll(
                title,
                new Separator(),
                new Label("Planifier un entrainement"),
                form,
                createRow,
                new Separator(),
                upcomingLabel,
                filterRow,
                trainingTable,
                new Separator(),
                participationLabel,
                participationRow,
                participationList,
                messageLabel
        );

        return root;
    }

    public LocalDate getTrainingDate() {
        return datePicker != null ? datePicker.getValue() : null;
    }

    public String getTrainingTime() {
        return timeField != null ? timeField.getText() : "";
    }

    public String getTrainingLocation() {
        return locationField != null ? locationField.getText() : "";
    }

    public String getTrainingActivity() {
        return activityField != null ? activityField.getText() : "";
    }

    public String getClubId() {
        return clubIdField != null ? clubIdField.getText() : "";
    }

    public LocalDate getFromDate() {
        return fromDatePicker != null ? fromDatePicker.getValue() : null;
    }

    public int getSelectedTrainingId() {
        if (trainingTable == null || trainingTable.getSelectionModel().getSelectedItem() == null) {
            return -1;
        }
        Entrainement selected = trainingTable.getSelectionModel().getSelectedItem();
        return selected.getId() != null ? selected.getId() : -1;
    }

    public String getParticipationUserId() {
        return participationUserIdField != null ? participationUserIdField.getText() : "";
    }

    public ParticipationStatus getSelectedStatus() {
        ParticipationStatus status = statusChoiceBox != null ? statusChoiceBox.getValue() : null;
        return status != null ? status : ParticipationStatus.PENDING;
    }

    public void setTrainings(List<Entrainement> trainings) {
        if (trainingTable == null) {
            return;
        }
        trainingTable.setItems(FXCollections.observableArrayList(trainings));
    }

    public void setParticipation(Map<User, ParticipationStatus> participation) {
        if (participationList == null) {
            return;
        }
        List<String> rows = new ArrayList<>();
        for (Map.Entry<User, ParticipationStatus> entry : participation.entrySet()) {
            User user = entry.getKey();
            ParticipationStatus status = entry.getValue();
            rows.add(user.getId() + " - " + user.getName() + " (" + status + ")");
        }
        participationList.setItems(FXCollections.observableArrayList(rows));
    }

    public void clearParticipation() {
        if (participationList != null) {
            participationList.getItems().clear();
        }
    }

    public void clearTrainingForm() {
        if (datePicker != null) {
            datePicker.setValue(null);
        }
        if (timeField != null) {
            timeField.clear();
        }
        if (locationField != null) {
            locationField.clear();
        }
        if (activityField != null) {
            activityField.clear();
        }
    }

    public void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            messageLabel.setText(message);
        }
    }

    public void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            messageLabel.setText(message);
        }
    }
}
