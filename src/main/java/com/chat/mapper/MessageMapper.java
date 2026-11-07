package com.chat.mapper;

import com.chat.dto.MessageResponse;
import com.chat.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderUsername(message.getSender().getUsername())
                .roomId(message.getRoom().getId())
                .roomName(message.getRoom().getName())
                .sentAt(message.getSentAt())
                .build();
    }
}
