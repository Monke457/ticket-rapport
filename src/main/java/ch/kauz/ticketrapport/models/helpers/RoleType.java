package ch.kauz.ticketrapport.models.helpers;

/**
 * Represents the possible role database values.
 * <p>
 *     To centralize the values for roles and avoid having to hardcode them when performing database queries.
 *     <br>
 *     <strong>Changing the display values will require the values in the database to be updated.</strong>
 * </p>
 */
public enum RoleType {
    LEARNER("Lernende"),
    ADMIN("Admin");

    private final String display;

    RoleType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
