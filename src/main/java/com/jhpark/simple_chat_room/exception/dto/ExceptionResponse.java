package com.jhpark.simple_chat_room.exception.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExceptionResponse {

    private final String message;
    
}
