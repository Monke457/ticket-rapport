package ch.kauz.ticketrapport.security;

import ch.kauz.ticketrapport.models.User;
import ch.kauz.ticketrapport.models.helpers.RoleType;
import ch.kauz.ticketrapport.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Custom user details service to load authenticated user details in to session context.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    /**
     * Finds user data by email in the persistence context and loads it into the session context.
     *
     * @see UserDetailsService#loadUserByUsername
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username);
        if (user == null) {
            throw new EntityNotFoundException("Bad credentials");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuths(user));
    }

    /**
     * Creates permissions of the session user.
     *
     * @param user the user to create permissions for
     * @return a set of {@link GrantedAuthority} objects
     */
    private Set<GrantedAuthority> getAuths(User user) {
        Set<GrantedAuthority> auths = new HashSet<>();
        for (RoleType role : RoleType.values()) {
            if (role.getDisplay().equals(user.getRole().getDescription())) {
                auths.add(new SimpleGrantedAuthority("ROLE_" + role));
                break;
            }
        }
        return auths;
    }
}
