package com.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {

    @NotBlank(message = "Message content must not be blank")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String content;
}
