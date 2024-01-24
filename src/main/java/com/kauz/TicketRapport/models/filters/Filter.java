package com.kauz.TicketRapport.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private String search;

    // multiple sort order separated by commas
    private String sort;
    private int page;
    private boolean asc;
}
