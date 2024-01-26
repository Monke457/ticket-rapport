package com.kauz.TicketRapport.security;

import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.services.DBServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * A helper class for retrieving current user data from the session context.
 */
@Component
public class AuthUser {
    @Autowired
    private DBServices DBServices;

    /**
     * Retrieves user information from the current session context.
     * @return a user object representing the current session user.
     */
    public User getUser() {
        return DBServices.getUserService().findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
