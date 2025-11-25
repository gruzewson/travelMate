package org.travelmate.controller.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.travelmate.model.ChatMessage;
import org.travelmate.model.ChatMessageEvent;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocket endpoint for real-time chat message delivery.
 * Observes CDI ChatMessageEvent and sends messages to connected clients.
 */
@ApplicationScoped
@ServerEndpoint("/chat/{username}")
public class ChatWebSocket {

    private static final Logger LOGGER = Logger.getLogger(ChatWebSocket.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Store active sessions mapped by username
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        LOGGER.info("[WEBSOCKET] User connected: " + username + " (Total: " + sessions.size() + ")");
        
        // Notify others about new user
        broadcastSystemMessage(username + " joined the chat");
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        LOGGER.info("[WEBSOCKET] User disconnected: " + username + " (Total: " + sessions.size() + ")");
        
        // Notify others about user leaving
        broadcastSystemMessage(username + " left the chat");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        LOGGER.log(Level.WARNING, "[WEBSOCKET] Error for user " + username + ": " + throwable.getMessage(), throwable);
        sessions.remove(username);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        // Messages are sent via AJAX/JSF, not directly through WebSocket
        // This handler is here for completeness but shouldn't be used
        LOGGER.info("[WEBSOCKET] Received message from " + username + ": " + message);
    }

    /**
     * CDI Event observer - this method is called when ChatService fires a ChatMessageEvent
     */
    public void onChatMessage(@Observes ChatMessageEvent event) {
        ChatMessage message = event.getMessage();
        LOGGER.info("[WEBSOCKET] Observed CDI event - sending message from " + message.getSenderUsername());

        String jsonMessage = formatMessageAsJson(message);

        if (message.isBroadcast()) {
            // Send to all connected users
            broadcastMessage(jsonMessage);
        } else {
            // Send to specific recipient and sender
            sendToUser(message.getRecipientUsername(), jsonMessage);
            // Also send to sender so they see their own message
            if (!message.getSenderUsername().equals(message.getRecipientUsername())) {
                sendToUser(message.getSenderUsername(), jsonMessage);
            }
        }
    }

    private void broadcastMessage(String jsonMessage) {
        sessions.values().forEach(session -> {
            sendMessage(session, jsonMessage);
        });
    }

    private void broadcastSystemMessage(String text) {
        String json = String.format(
            "{\"type\":\"system\",\"content\":\"%s\",\"timestamp\":\"%s\"}",
            escapeJson(text),
            java.time.LocalDateTime.now().format(TIME_FORMATTER)
        );
        broadcastMessage(json);
    }

    private void sendToUser(String username, String jsonMessage) {
        Session session = sessions.get(username);
        if (session != null && session.isOpen()) {
            sendMessage(session, jsonMessage);
        }
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[WEBSOCKET] Failed to send message: " + e.getMessage(), e);
        }
    }

    private String formatMessageAsJson(ChatMessage message) {
        return String.format(
            "{\"type\":\"chat\",\"id\":\"%s\",\"sender\":\"%s\",\"recipient\":\"%s\",\"content\":\"%s\",\"timestamp\":\"%s\",\"broadcast\":%b}",
            message.getId().toString(),
            escapeJson(message.getSenderUsername()),
            message.getRecipientUsername() != null ? escapeJson(message.getRecipientUsername()) : "",
            escapeJson(message.getContent()),
            message.getTimestamp().format(TIME_FORMATTER),
            message.isBroadcast()
        );
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    /**
     * Get list of currently connected users (for UI)
     */
    public static java.util.Set<String> getConnectedUsers() {
        return sessions.keySet();
    }
}
