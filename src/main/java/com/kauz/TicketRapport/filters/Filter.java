package com.kauz.TicketRapport.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A general filter class containing all the universal filter, sorting and pagination attributes.
 * Any entity specific filter should extend here to inherit the general attributes.
 * Has a search string, a sort string the current page number and a boolean check for the direction of sorting.
 */
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

    /**
     * Returns a representation of all the filter attributes except the page number in the form of url query parameters.
     * includes a new page number in the query string.
     *
     * @param newPage the new page number.
     * @return a string of the filter attributes as a url query.
     */
    public String toQueryString(Integer newPage) {
        return "?search=" + search
                + "&sort=" + sort
                + "&page=" + newPage
                + "&asc=" + asc;
    }

    /**
     * Returns a representation of all the filter attributes except the sort string in the form of url query parameters.
     * includes a new sort order in the query string, updates the sort direction accordingly.
     *
     * @param newSort the new sort order.
     * @return a string of the filter attributes as a url query.
     */
    public String toQueryString(String newSort) {
        return "?search=" + search
                + "&sort=" + newSort
                + "&page=" + page
                + "&asc=" + (!newSort.equals(sort) || !asc);
    }
}
