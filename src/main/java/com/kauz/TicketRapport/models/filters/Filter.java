package com.kauz.TicketRapport.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Filter {
    private String search;
    private String sort;
    private int page;
    private boolean asc;
}
