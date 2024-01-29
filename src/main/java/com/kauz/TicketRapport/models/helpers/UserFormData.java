package com.kauz.TicketRapport.models.helpers;

import com.kauz.TicketRapport.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Email(message = "Please enter a valid email address")
    @NotBlank(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Please enter a password")
    @Size(min = 5, max=50, message = "The password must be between 5 and 50 characters long")
    private String password;

    @NotBlank(message = "Please confirm the password")
    @Size(min = 5, max=50, message = "The password must be between 5 and 50 characters long")
    private String confirmPassword;

    @NotNull(message = "Please select a role")
    private Role role;
}
