package com.kauz.TicketRapport.models.helpers;

import com.kauz.TicketRapport.models.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
