package org.travelmate.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.travelmate.model.ChatMessage;
import org.travelmate.model.ChatMessageEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Chat service that handles message sending via CDI events.
 * Messages are fired as CDI events and observed by the WebSocket endpoint.
 */
@ApplicationScoped
public class ChatService {

    private static final Logger LOGGER = Logger.getLogger(ChatService.class.getName());
    private static final int MAX_HISTORY_SIZE = 100;

    @Inject
    private Event<ChatMessageEvent> messageEvent;

    // Store recent messages for history (new users can see recent messages)
    private final List<ChatMessage> messageHistory = Collections.synchronizedList(new ArrayList<>());

    /**
     * Send a message to all users (broadcast)
     */
    public void sendBroadcast(String senderUsername, String content) {
        ChatMessage message = new ChatMessage(senderUsername, null, content);
        sendMessage(message);
    }

    /**
     * Send a private message to a specific user
     */
    public void sendPrivate(String senderUsername, String recipientUsername, String content) {
        ChatMessage message = new ChatMessage(senderUsername, recipientUsername, content);
        sendMessage(message);
    }

    /**
     * Send a message and fire CDI event
     */
    public void sendMessage(ChatMessage message) {
        LOGGER.info(String.format("[CHAT] %s -> %s: %s", 
            message.getSenderUsername(),
            message.isBroadcast() ? "ALL" : message.getRecipientUsername(),
            message.getContent()));

        // Add to history
        addToHistory(message);

        // Fire CDI event - this will be observed by WebSocket endpoint
        messageEvent.fire(new ChatMessageEvent(message));
    }

    private void addToHistory(ChatMessage message) {
        messageHistory.add(message);
        // Keep only last MAX_HISTORY_SIZE messages
        while (messageHistory.size() > MAX_HISTORY_SIZE) {
            messageHistory.remove(0);
        }
    }

    /**
     * Get recent broadcast messages for history display
     */
    public List<ChatMessage> getRecentBroadcastMessages() {
        return messageHistory.stream()
                .filter(ChatMessage::isBroadcast)
                .toList();
    }

    /**
     * Get messages visible to a specific user (broadcasts + their private messages)
     */
    public List<ChatMessage> getMessagesForUser(String username) {
        return messageHistory.stream()
                .filter(m -> m.isFor(username))
                .toList();
    }
}
