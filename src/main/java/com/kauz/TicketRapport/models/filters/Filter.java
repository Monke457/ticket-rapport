package com.kauz.TicketRapport.models.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    public Filter(String search) {
        this.search = search;
    }

    public String toQueryString(Integer newPage) {
        return "?search" + search
                + "&sort=" + sort
                + "&page=" + newPage
                + "&asc=" + asc;
    }

    public String toQueryString(String newSort) {
        return "?search" + search
                + "&sort=" + newSort
                + "&page=" + page
                + "&asc=" + (!newSort.equals(sort) || !asc);
    }
}
