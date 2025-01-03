package com.jhpark.simple_chat_room.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {

            return Long.parseLong((String)authentication.getPrincipal());
            
        }
        throw new RuntimeException("USER NOT AUTHORIZED.");
    }

    
}