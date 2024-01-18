package com.kauz.TicketRapport.security;

import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.services.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUser {
    @Autowired
    private UnitOfWork unitOfWork;

    public User getUser() {
        return unitOfWork.getUserService().findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
