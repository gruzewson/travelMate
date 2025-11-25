package org.travelmate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * CDI Event class for chat messages.
 * This event is fired when a new message is sent and observed by the WebSocket endpoint.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent implements Serializable {

    private ChatMessage message;

}
