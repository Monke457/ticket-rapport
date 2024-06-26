package ch.kauz.ticketrapport.security;

import ch.kauz.ticketrapport.models.helpers.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web security configuration - defines authorization parameters for http requests.
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    /**
     * Defines a password encoder bean which can be injected where needed.
     * @return a {@link BCryptPasswordEncoder} object
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the security parameters for https requests in a filter chain.
     * <p>
     *     Restricts most areas of the app to admins only.
     *     <br>
     *     Allows learners to see tickets details and access protocol handlers.
     * </p>
     * @param http a {@link HttpSecurity} object which allows for authorization definitions
     * @return a {@link SecurityFilterChain} interface capable of being matched against http requests
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/tickets/details").authenticated()
                        .requestMatchers("/tickets/protocol").hasRole(RoleType.LEARNER.toString())
                        .requestMatchers("/tickets/reopen").hasRole(RoleType.LEARNER.toString())
                        .requestMatchers("/archive/**").hasRole(RoleType.ADMIN.toString())
                        .requestMatchers("/tickets/**").hasRole(RoleType.ADMIN.toString())
                        .requestMatchers("/clients/**").hasRole(RoleType.ADMIN.toString())
                        .requestMatchers("/checklists/**").hasRole(RoleType.ADMIN.toString())
                        .requestMatchers("/users/**").hasRole(RoleType.ADMIN.toString())
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll())
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
