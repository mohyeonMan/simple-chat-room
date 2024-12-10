package com.jhpark.simple_chat_room.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {

            return ((Long) authentication.getPrincipal());
            
        }
        
        throw new RuntimeException("인증되지 않은 사용자입니다.");
    }

    
}