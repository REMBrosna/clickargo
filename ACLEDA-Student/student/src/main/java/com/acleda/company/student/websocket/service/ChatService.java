package com.acleda.company.student.websocket.service;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.administrator.repository.AppUserRepository;
import com.acleda.company.student.websocket.model.TChatMessage;
import com.acleda.company.student.websocket.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    public void sendMessage(String senderUsername, String receiverUsername, String content) {
        TAppUser sender = appUserRepository.findAppUserByUsername(senderUsername);
        TAppUser receiver = appUserRepository.findAppUserByUsername(receiverUsername);

        if (sender != null && receiver != null) {
            TChatMessage message = new TChatMessage();
            message.setUserSender(sender);
            message.setUserReceiver(receiver);
            message.setContent(content);
            message.setType(TChatMessage.MessageType.CHAT);

            chatMessageRepository.save(message);
        }
    }

    public List<TChatMessage> getMessages(String senderUsername, String receiverUsername) {
        TAppUser sender = appUserRepository.findAppUserByUsername(senderUsername);
        TAppUser receiver = appUserRepository.findAppUserByUsername(receiverUsername);

        if (sender != null && receiver != null) {
            return chatMessageRepository.findChatHistoryBetweenUsers(receiver, sender);
        }

        return Collections.emptyList();
    }

    public List<TChatMessage> getMessagesFromSender(String senderUsername) {
        TAppUser sender = appUserRepository.findAppUserByUsername(senderUsername);
        if (sender != null) {
            return chatMessageRepository.findByUserReceiver(sender);
        }

        return Collections.emptyList();
    }

    public void debugOnlineUsers() {
        simpUserRegistry.getUsers().forEach(user -> {
            System.out.println("Connected user: " + user.getName());
        });
    }
}
