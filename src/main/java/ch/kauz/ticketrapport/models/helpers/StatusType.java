package ch.kauz.ticketrapport.models.helpers;

/**
 * Represents the possible status database values.
 * <p>
 *     To centralize the values for statuses and avoid having to hardcode them when performing database queries.
 *     <br>
 *     <strong>Changing the display values will require the values in the database to be updated.</strong>
 * </p>
 */
public enum StatusType {
    OPEN("In Bearbeitung"),
    COMPLETED("Beendet"),
    CLOSED("Geschlossen");

    private final String display;

    StatusType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
