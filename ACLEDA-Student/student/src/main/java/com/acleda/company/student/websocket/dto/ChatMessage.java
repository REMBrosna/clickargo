package com.acleda.company.student.websocket.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private String receiver ;
    private MessageType type;

    @Override
    public String toString() {
        return "ChatMessage{" +
                "content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", type=" + type +
                '}';
    }
}
