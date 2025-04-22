package com.acleda.company.student.websocket.repository;


import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.websocket.model.TChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<TChatMessage, String> {
    @Query("SELECT o FROM TChatMessage o WHERE " +
            "(o.userSender = :user1 AND o.userReceiver = :user2) " +
            "OR (o.userSender = :user2 AND o.userReceiver = :user1) ORDER BY o.dtCreate ASC")
    List<TChatMessage> findChatHistoryBetweenUsers(TAppUser user1, TAppUser user2);

    List<TChatMessage> findByUserReceiver(TAppUser userSender);
}
