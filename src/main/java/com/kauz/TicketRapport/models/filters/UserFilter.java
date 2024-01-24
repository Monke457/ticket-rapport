package com.kauz.TicketRapport.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter extends Filter {
    private UUID roleId;
    private String role;

    public UserFilter(String search, String sort, int page, boolean asc, UUID roleId, String role) {
        super(search, sort, page, asc);
        this.roleId = roleId;
        this.role = role;
    }
}
