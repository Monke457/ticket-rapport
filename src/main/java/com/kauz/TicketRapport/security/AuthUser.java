package com.kauz.TicketRapport.security;

import com.kauz.TicketRapport.model.User;
import com.kauz.TicketRapport.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUser {
    @Autowired
    private UserService service;

    public User getUser() {
        return service.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
