package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.validation.constraints.FieldsMatch;
import ch.kauz.ticketrapport.validation.constraints.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Data Transfer Object representing the user entity
 * <p>
 *     Contains information pertaining to the user entity as well as a confirm password field.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@FieldsMatch(field = "password", fieldMatch = "passwordConfirm", message = "Die Passwörter stimmen nicht überein")
public class UserDTO {
    private UUID id;

    @NotBlank(message = "Bitte geben Sie einen Vorname an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 leigen")
    private String firstname;

    @NotBlank(message = "Bitte geben Sie einen Nachname an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 leigen")
    private String lastname;

    @UniqueEmail
    @Email(message = "Bitte geben Sie eine gültige Email-Adresse an")
    @NotBlank(message = "Bitte geben Sie eine gültige Email-Adresse an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 leigen")
    private String email;

    @NotBlank(message = "Bitte geben Sie ein Passwort an")
    @Size(min=5, max=50, message = "Die Grösse muss zwischen 5 und 50 leigen")
    private String password;

    @NotBlank(message = "Bitte bestätigen Sie ihr Passwort an")
    @Size(min=5, max=50, message = "Die Grösse muss zwischen 5 und 50 leigen")
    private String passwordConfirm;

    @NotNull(message = "Bitte wählen Sie eine Rolle aus")
    private UUID roleId;
}
