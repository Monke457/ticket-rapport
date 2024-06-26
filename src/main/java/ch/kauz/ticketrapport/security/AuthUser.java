package ch.kauz.ticketrapport.security;

import ch.kauz.ticketrapport.models.User;
import ch.kauz.ticketrapport.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Represents the authenticated user in the session context.
 */
@Component
public class AuthUser {
    @Autowired
    private UserService userService;

    /**
     * Gets the user's name from the session context and fetches user data from the persistence context.
     *
     * @return the current session {@link User}
     */
    public User getUser() {
        return userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
