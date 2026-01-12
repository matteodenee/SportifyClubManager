package com.sportify.manager.frame;

import com.sportify.manager.controllers.CommunicationController;
import com.sportify.manager.services.NetConversation;
import com.sportify.manager.services.User;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.CheckBoxListCell;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class CommunicationFrame {

    private final ListView<NetConversation> conversationsList = new ListView<>();
    private final TextArea messagesArea = new TextArea();
    private final TextField inputField = new TextField();
    private final Button sendBtn = new Button("Envoyer");

    private final TextField groupNameField = new TextField();
    private final Button createGroupBtn = new Button("Créer groupe");
    private final ListView<User> membersList = new ListView<>();
    private final Label membersLabel = new Label("Membres du club");
    private final Map<String, BooleanProperty> memberSelection = new HashMap<>();

    private final Label statusLabel = new Label();

    private final CommunicationController controller;

    public CommunicationFrame(Stage stage) {
        controller = new CommunicationController(this);

        messagesArea.setEditable(false);
        messagesArea.setWrapText(true);

        inputField.setPromptText("Écrire un message...");
        groupNameField.setPromptText("Nom du groupe");

        membersList.setCellFactory(CheckBoxListCell.forListView(user -> {
            BooleanProperty selected = memberSelection.get(user.getId());
            if (selected == null) {
                selected = new SimpleBooleanProperty(false);
                memberSelection.put(user.getId(), selected);
            }
            return selected;
        }, new StringConverter<>() {
            @Override
            public String toString(User user) {
                if (user == null) {
                    return "";
                }
                return user.getName() + " (" + user.getId() + ")";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        }));

        VBox left = new VBox(8,
                new Label("Conversations"),
                conversationsList,
                new Separator(),
                groupNameField,
                membersLabel,
                membersList,
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
    public void setClubMembers(java.util.List<User> members) {
        memberSelection.clear();
        for (User user : members) {
            if (user != null) {
                memberSelection.put(user.getId(), new SimpleBooleanProperty(false));
            }
        }
        membersList.setItems(FXCollections.observableArrayList(members));
    }
    public java.util.List<String> getSelectedMemberIds() {
        return memberSelection.entrySet().stream()
                .filter(entry -> entry.getValue().get())
                .map(Map.Entry::getKey)
                .toList();
    }
    public void setGroupControlsVisible(boolean visible) {
        groupNameField.setVisible(visible);
        groupNameField.setManaged(visible);
        createGroupBtn.setVisible(visible);
        createGroupBtn.setManaged(visible);
        membersList.setVisible(visible);
        membersList.setManaged(visible);
        membersLabel.setVisible(visible);
        membersLabel.setManaged(visible);
    }
}
