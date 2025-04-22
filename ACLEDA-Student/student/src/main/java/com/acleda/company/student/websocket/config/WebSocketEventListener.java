package com.acleda.company.student.websocket.config;

import com.acleda.company.student.websocket.dto.ChatMessage;
import com.acleda.company.student.websocket.dto.MessageType;
import com.acleda.company.student.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatService chatService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        if (username != null) {
            log.info("user disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("New WebSocket connection");
        chatService.debugOnlineUsers();
    }
    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        Principal user = event.getUser();
        System.out.println("🔌 CONNECTED: " + (user != null ? user.getName() : "null"));
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        System.out.println("❌ DISCONNECTED: " + (user != null ? user.getName() : "null"));
    }
}
