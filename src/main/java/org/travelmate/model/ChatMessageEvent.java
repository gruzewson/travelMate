package org.travelmate.model;

import java.io.Serializable;

/**
 * CDI Event class for chat messages.
 * This event is fired when a new message is sent and observed by the WebSocket endpoint.
 */
public class ChatMessageEvent implements Serializable {

    private ChatMessage message;

    public ChatMessageEvent() {
    }

    public ChatMessageEvent(ChatMessage message) {
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }
}
