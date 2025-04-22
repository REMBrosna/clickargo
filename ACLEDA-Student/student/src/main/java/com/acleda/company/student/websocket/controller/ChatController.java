package com.acleda.company.student.websocket.controller;

import com.acleda.company.student.websocket.dto.ChatMessage;
import com.acleda.company.student.websocket.model.TChatMessage;
import com.acleda.company.student.websocket.repository.ChatMessageRepository;
import com.acleda.company.student.websocket.service.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@Log4j2
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public TChatMessage sendMessage(@Payload TChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // Add username in web socket session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
        return chatMessage;
    }
    @MessageMapping("/chat.sendPrivate")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage, Principal principal) {
        log.info("sender :{}" , chatMessage.getSender());
        // Save message to DB
        chatService.sendMessage(chatMessage.getSender(),
                chatMessage.getReceiver(), chatMessage.getContent());

        // Send message to recipient
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(), "/queue/messages", chatMessage);
    }

    @MessageMapping("/chat.getMessages")
    public void getMessages(@Payload ChatMessage chatMessage) {
        List<TChatMessage> messages = chatService.getMessages(
                chatMessage.getSender(),
                chatMessage.getReceiver()
        );
        log.info("chatMessage.getSender() : {}", chatMessage.getSender());
        // Send to the sender (who requested the history)
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSender(),
                "/queue/messages",
                messages
        );
        log.info("Get message history for sender: {}, receiver: {}", chatMessage.getSender(), chatMessage.getReceiver());
        log.info("Messages: {}", messages);
    }

    @GetMapping("/receiver/{username}")
    public ResponseEntity<?> findSenderMessage(@PathVariable String username) {
        List<TChatMessage> messages = chatService.getMessagesFromSender(username);
        return ResponseEntity.ok(messages);
    }
}
