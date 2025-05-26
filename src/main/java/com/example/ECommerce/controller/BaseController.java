package com.example.ECommerce.controller;

import com.example.ECommerce.entity.KeycloakUser;
import com.example.ECommerce.util.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseController {
    
    protected KeycloakUser getCurrentUser() {
        try {
            return JwtTokenUtil.getCurrentUser();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
    }
} 