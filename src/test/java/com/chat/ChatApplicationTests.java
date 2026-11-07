package com.chat;

import com.chat.dto.RegisterRequest;
import com.chat.dto.AuthResponse;
import com.chat.service.AuthService;
import com.chat.dto.RoomRequest;
import com.chat.dto.RoomResponse;
import com.chat.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ChatApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoomService roomService;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setPassword("password123");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldNotRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("dupuser");
        request.setEmail("dup1@example.com");
        request.setPassword("password123");
        authService.register(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUsername("dupuser");
        duplicate.setEmail("dup2@example.com");
        duplicate.setPassword("password123");

        assertThrows(Exception.class, () -> authService.register(duplicate));
    }

    @Test
    void shouldCreateRoom() {
        RoomRequest request = new RoomRequest();
        request.setName("general");
        request.setDescription("General discussion");

        RoomResponse response = roomService.createRoom(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("general");
        assertThat(response.getTotalMessages()).isEqualTo(0);
    }
}
