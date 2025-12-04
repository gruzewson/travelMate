package org.travelmate.controller.viewbean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.travelmate.controller.websocket.ChatWebSocket;
import org.travelmate.model.User;
import org.travelmate.service.ChatService;
import org.travelmate.service.UserService;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * JSF managed bean for chat functionality.
 */
@Named
@ViewScoped
public class ChatBean implements Serializable {

    @Inject
    private ChatService chatService;

    @Inject
    private UserService userService;

    @Inject
    private AuthBean authBean;

    @Getter
    @Setter
    private String messageContent;

    @Getter
    @Setter
    private String selectedRecipient;

    @PostConstruct
    public void init() {
        messageContent = "";
        selectedRecipient = "";
    }

    public void sendMessage() {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return;
        }

        String sender = authBean.getUsername();
        if (sender == null) {
            return;
        }

        String content = messageContent.trim();

        if (selectedRecipient == null || selectedRecipient.trim().isEmpty()) {
            chatService.sendBroadcast(sender, content);
        } else {
            chatService.sendPrivate(sender, selectedRecipient.trim(), content);
        }

        messageContent = "";
    }

    public String getCurrentUsername() {
        return authBean.getUsername();
    }

    public List<User> getAllUsers() {
        String currentUser = authBean.getUsername();
        return userService.findAll().stream()
                .filter(user -> !user.getLogin().equals(currentUser))
                .toList();
    }

    public Set<String> getConnectedUsers() {
        return ChatWebSocket.getConnectedUsers();
    }

    public boolean isUserOnline(String username) {
        return ChatWebSocket.getConnectedUsers().contains(username);
    }
}

