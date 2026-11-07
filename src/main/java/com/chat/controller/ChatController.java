package com.chat.controller;

import com.chat.dto.MessageRequest;
import com.chat.dto.MessageResponse;
import com.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket: client sends to /app/chat/{roomId}
    // All subscribers to /topic/room/{roomId} receive the message
    @MessageMapping("/chat/{roomId}")
    public void sendMessageViaWebSocket(@DestinationVariable Long roomId,
                                        @Payload MessageRequest request,
                                        java.security.Principal principal) {
        MessageResponse response = chatService.sendMessage(roomId, principal.getName(), request);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, response);
    }

    // REST fallback: POST /api/rooms/{roomId}/messages
    @PostMapping("/api/rooms/{roomId}/messages")
    public ResponseEntity<MessageResponse> sendMessageViaRest(
            @PathVariable Long roomId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MessageResponse response = chatService.sendMessage(roomId, userDetails.getUsername(), request);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/rooms/{roomId}/messages — message history with pagination
    @GetMapping("/api/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getHistory(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(chatService.getMessageHistory(roomId, page, size));
    }
}
