package com.chat.mapper;

import com.chat.dto.RoomRequest;
import com.chat.dto.RoomResponse;
import com.chat.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponse toResponse(Room room, int totalMessages) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .totalMessages(totalMessages)
                .createdAt(room.getCreatedAt())
                .build();
    }

    public Room toEntity(RoomRequest request) {
        return Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
