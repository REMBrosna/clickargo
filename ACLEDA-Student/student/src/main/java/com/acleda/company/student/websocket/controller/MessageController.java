//package com.acleda.company.student.websocket.controller;
//
//
//import com.acleda.company.student.websocket.dto.ChatMessage;
//import com.acleda.company.student.websocket.model.TChatMessage;
//import com.acleda.company.student.websocket.repository.MessageRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping()
//@AllArgsConstructor
//@CrossOrigin
//public class MessageController {
//
//    @Autowired
//    @Qualifier("simpMessagingTemplate")
//    private SimpMessagingTemplate messagingTemplate;
//    private final MessageRepository messageRepository;
//
//    // This endpoint is used for sending a message
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(TChatMessage message) {
//        System.out.println("🔥 Received message: " + message.getContent());
//
//        try {
//            // Save the message to the database
//            messageRepository.save(message);
//        } catch (Exception e) {
//            System.err.println("Error saving message: " + e.getMessage());
//        }
//
//        // Send the message to the specific receiver using their session ID
//        messagingTemplate.convertAndSendToUser(
//                message.getUserReceiver().getId().toString(),  // Use receiver's user ID
//                "/queue/messages",  // Send to receiver's personal queue
//                message  // The actual message content
//        );
//    }
//
//    // This is used to handle when a user joins the chat (optional functionality)
//    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/public")
//    public ChatMessage addUser(ChatMessage message) {
//        message.setContent(message.getSender() + " joined the chat.");
//        return message;
//    }
//
//    // Fetch all messages (to be used in frontend to get chat history)
//    @GetMapping("/")
//    public ResponseEntity<List<TChatMessage>> getMessages() {
//        List<TChatMessage> messages = messageRepository.findAll();
//        return ResponseEntity.ok(messages);
//    }
//
//    // Fetch messages between a specific sender and receiver
//    @GetMapping("/{sender}/{receiver}")
//    public ResponseEntity<?> findSenderAndReceiverMessage(@PathVariable Long sender, @PathVariable Long receiver) {
//        List<TChatMessage> messages = messageRepository.findTChatMessageBySenderIdAndUserReceiverId(sender, receiver);
//        return ResponseEntity.ok(messages);
//    }
//
//    // Fetch messages by receiver ID (useful for showing all messages of a receiver)
//    @GetMapping("/receiver/{receiver}")
//    public ResponseEntity<Object> findReceiverMessage(@PathVariable Long receiver) {
//        List<TChatMessage> messages = messageRepository.findTChatMessageByUserReceiverId(receiver);
//        return ResponseEntity.ok(messages);
//    }
//}
