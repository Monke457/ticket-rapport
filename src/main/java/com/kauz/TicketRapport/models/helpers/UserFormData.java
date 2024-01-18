package com.kauz.TicketRapport.models.helpers;

import com.kauz.TicketRapport.models.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User DTO containing all User attributes as well as password and confirm password.
 * For creating new users and assigning a password.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserFormData {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;
}
