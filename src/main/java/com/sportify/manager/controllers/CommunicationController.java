package com.sportify.manager.controllers;

import com.sportify.manager.facade.CommunicationFacade;
import com.sportify.manager.frame.CommunicationFrame;
import com.sportify.manager.services.NetConversation;
import com.sportify.manager.services.NetConversationType;
import com.sportify.manager.services.NetMessage;
import javafx.collections.FXCollections;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommunicationController implements CommunicationListener {

    private final CommunicationFrame view;
    private final CommunicationFacade facade = CommunicationFacade.getInstance();
    private long currentConversationId = -1;

    private final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    public CommunicationController(CommunicationFrame view) {
        this.view = view;
        facade.addListener(this);
    }

    public void onOpen() {
        view.getStatusLabel().setText("Connexion au chat...");
        facade.connect("localhost", 5555);
    }

    public void onSendClicked() {
        String text = view.getInputField().getText();
        view.getInputField().clear();
        facade.sendMessage(text);
    }

    public void onCreateGroupClicked() {
        String name = view.getGroupNameField().getText();
        view.getGroupNameField().clear();
        facade.createGroup(name);
    }

    public void onConversationSelected(NetConversation conv) {
        currentConversationId = conv.id();
        view.getStatusLabel().setText("Conversation: " + conv.name());
        facade.selectConversation(conv.id());
    }

    @Override
    public void onConversations(List<NetConversation> conversations) {
        view.getConversationsList().setItems(FXCollections.observableArrayList(conversations));

        if (currentConversationId <= 0) {
            for (NetConversation c : conversations) {
                if (c.type() == NetConversationType.GLOBAL) {
                    view.getConversationsList().getSelectionModel().select(c);
                    break;
                }
            }
        }
    }

    @Override
    public void onHistory(long conversationId, List<NetMessage> history) {
        if (conversationId != currentConversationId) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (NetMessage m : history) {
            sb.append("[")
                    .append(fmt.format(m.sentAt()))
                    .append("] ")
                    .append(m.senderId())
                    .append(": ")
                    .append(m.content())
                    .append("\n");
        }
        view.getMessagesArea().setText(sb.toString());
    }

    @Override
    public void onNewMessage(NetMessage message) {
        if (message.conversationId() != currentConversationId) {
            return;
        }

        String line = "[" + fmt.format(message.sentAt()) + "] "
                + message.senderId() + ": " + message.content() + "\n";
        view.getMessagesArea().appendText(line);
    }

    @Override
    public void onError(String message) {
        view.getStatusLabel().setText("Erreur: " + message);
    }

    @Override
    public void onSystem(String message) {
        view.getStatusLabel().setText(message);
    }
}
