package ch.kauz.ticketrapport.filters;

import ch.kauz.ticketrapport.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * A user specific filter that inherits all the general filter fields.
 * <p>
 *     May also include a client id, status id and status string.
 * </p>
 * @see Filter
 * @see User
 */
@Getter
@Setter
public class UserFilter extends Filter {
    private UUID roleId;

    @Override
    public String toQueryString(String sort) {
        return super.toQueryString(sort) +
                (roleId == null ? "" : "&roleId=" + roleId);
    }

    @Override
    public String toQueryString(int page) {
        return super.toQueryString(page) +
                (roleId == null ? "" : "&roleId=" + roleId);
    }
}
