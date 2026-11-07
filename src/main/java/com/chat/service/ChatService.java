package com.chat.service;

import com.chat.dto.MessageRequest;
import com.chat.dto.MessageResponse;
import com.chat.entity.Message;
import com.chat.entity.Room;
import com.chat.entity.User;
import com.chat.exception.UserNotFoundException;
import com.chat.mapper.MessageMapper;
import com.chat.repository.MessageRepository;
import com.chat.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageResponse sendMessage(Long roomId, String senderUsername, MessageRequest request) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UserNotFoundException(senderUsername));

        Room room = roomService.findRoomOrThrow(roomId);

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .room(room)
                .build();

        return messageMapper.toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getMessageHistory(Long roomId, int page, int size) {
        roomService.findRoomOrThrow(roomId);

        Page<Message> messages = messageRepository
                .findByRoomIdOrderBySentAtDesc(roomId, PageRequest.of(page, size));

        return messages.getContent()
                .stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }
}
