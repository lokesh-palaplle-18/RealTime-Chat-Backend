package com.chat.service;

import com.chat.dto.RoomRequest;
import com.chat.dto.RoomResponse;
import com.chat.entity.Room;
import com.chat.exception.RoomAlreadyExistsException;
import com.chat.exception.RoomNotFoundException;
import com.chat.mapper.RoomMapper;
import com.chat.repository.MessageRepository;
import com.chat.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final RoomMapper roomMapper;

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new RoomAlreadyExistsException(request.getName());
        }
        Room saved = roomRepository.save(roomMapper.toEntity(request));
        return roomMapper.toResponse(saved, 0);
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(room -> roomMapper.toResponse(room, messageRepository.countByRoomId(room.getId())))
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        Room room = findRoomOrThrow(id);
        return roomMapper.toResponse(room, messageRepository.countByRoomId(id));
    }

    @Transactional
    public void deleteRoom(Long id) {
        roomRepository.delete(findRoomOrThrow(id));
    }

    public Room findRoomOrThrow(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }
}
