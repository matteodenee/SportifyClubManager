package com.sportify.manager.frame;

import com.sportify.manager.controllers.CommunicationController;
import com.sportify.manager.services.NetConversation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CommunicationFrame {

    private final ListView<NetConversation> conversationsList = new ListView<>();
    private final TextArea messagesArea = new TextArea();
    private final TextField inputField = new TextField();
    private final Button sendBtn = new Button("Envoyer");

    private final TextField groupNameField = new TextField();
    private final Button createGroupBtn = new Button("Créer groupe");

    private final Label statusLabel = new Label();

    private final CommunicationController controller;

    public CommunicationFrame(Stage stage) {
        controller = new CommunicationController(this);

        messagesArea.setEditable(false);
        messagesArea.setWrapText(true);

        inputField.setPromptText("Écrire un message...");
        groupNameField.setPromptText("Nom du groupe");

        VBox left = new VBox(8,
                new Label("Conversations"),
                conversationsList,
                new Separator(),
                groupNameField,
                createGroupBtn
        );
        left.setPrefWidth(260);

        HBox bottom = new HBox(8, inputField, sendBtn);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(left);
        root.setCenter(messagesArea);
        root.setBottom(new VBox(6, bottom, statusLabel));

        sendBtn.setOnAction(e -> controller.onSendClicked());
        createGroupBtn.setOnAction(e -> controller.onCreateGroupClicked());
        conversationsList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                controller.onConversationSelected(newV);
            }
        });

        stage.setTitle("Sportify - Communication");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();

        controller.onOpen();
    }

    public ListView<NetConversation> getConversationsList() { return conversationsList; }
    public TextArea getMessagesArea() { return messagesArea; }
    public TextField getInputField() { return inputField; }
    public TextField getGroupNameField() { return groupNameField; }
    public Label getStatusLabel() { return statusLabel; }
}
