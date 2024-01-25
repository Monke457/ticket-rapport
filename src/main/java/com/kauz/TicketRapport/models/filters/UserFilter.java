package com.kauz.TicketRapport.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A filter class specifically for the user entity.
 * Inherits all the general filter, sorting and pagination attributes from the parent filter class.
 * May also has a role id and role description as a string.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter extends Filter {
    private UUID roleId;
    private String role;

    public UserFilter(String search, String sort, int page, boolean asc, UUID roleId) {
        super(search, sort, page, asc);
        this.roleId = roleId;
    }

    @Override
    public String toQueryString(Integer newPage) {
        return super.toQueryString(newPage)
                + "&roleId=" + (roleId == null ? "" : roleId);
    }

    @Override
    public String toQueryString(String newSort) {
        return super.toQueryString(newSort)
                + "&roleId=" + (roleId == null ? "" : roleId);
    }
}
