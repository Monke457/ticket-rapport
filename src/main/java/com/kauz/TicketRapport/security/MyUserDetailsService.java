package com.kauz.TicketRapport.security;

import com.kauz.TicketRapport.model.User;
import com.kauz.TicketRapport.services.UserService;
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

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = service.findByEmail(username);
        if (user == null) {
            throw new EntityNotFoundException("Email '" + username + "' not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuths(user));
    }

    private Set<GrantedAuthority> getAuths(User user) {
        Set<GrantedAuthority> auths = new HashSet<>();

        if (user.getRole() != null) {
            auths.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getDescription()));
        }

        return auths;
    }
}
