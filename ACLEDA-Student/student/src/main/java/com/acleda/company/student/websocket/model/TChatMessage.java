package com.acleda.company.student.websocket.model;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.infrastructure.domain.AbstractPersistableCustom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_CHAT_MESSAGE")
public class TChatMessage extends AbstractPersistableCustom {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SENDER_ID")
    private TAppUser userSender;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RECEIVER_ID")
    private TAppUser userReceiver;
    @JoinColumn(name = "CONTENT")
    private String content;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREATE", updatable = false)
    private Date dtCreate;
    private MessageType type;
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    @Override
    public String toString() {
        return "TChatMessage{" +
                "userSender=" + userSender +
                ", userReceiver=" + userReceiver +
                ", content='" + content + '\'' +
                ", dtCreate=" + dtCreate +
                ", type=" + type +
                '}';
    }
}
