package org.travelmate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

    private UUID id;
    private String senderUsername;
    private String recipientUsername; // null means broadcast to all
    private String content;
    private LocalDateTime timestamp;

    public ChatMessage(String senderUsername, String recipientUsername, String content) {
        this.id = UUID.randomUUID();
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isBroadcast() {
        return recipientUsername == null || recipientUsername.isEmpty();
    }

    public boolean isFor(String username) {
        return isBroadcast() || 
               senderUsername.equals(username) || 
               recipientUsername.equals(username);
    }
}
