package com.kauz.TicketRapport.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security configuration for handling http requests.
 * Handles authorization logic.
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/tickets/details*").authenticated()
                        .requestMatchers("/tickets*").hasRole("ADMIN")
                        .requestMatchers("/clients*").hasRole("ADMIN")
                        .requestMatchers("/users*").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(FormLoginConfigurer::permitAll)
                .logout(LogoutConfigurer::permitAll);
        return http.build();
    }
}
