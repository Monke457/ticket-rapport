package ch.kauz.ticketrapport.filters;

import lombok.Getter;
import lombok.Setter;

/**
 * Base filter object containing search, sort and pagination information.
 */
@Getter
@Setter
public class Filter {
    private String search = "";
    private int page = 1;
    private int pageSize = 10;
    private String sort = "";
    private boolean asc = true;

    /**
     * Builds URL query parameters from the field values and updated page number.
     * <p>
     *     This is called when navigating through the pagination.
     * </p>
     *
     * @param page the new page number
     * @return the filter data represented as a URL query string
     */
    public String toQueryString(int page) {
        return String.format("?search=%s&page=%s&sort=%s&asc=%s", this.search, page, this.sort, this.asc);
    }

    /**
     * Builds URL query parameters from the field values and updated page number sort field.
     * <p>
     *     This is called when sorting a table
     * </p>
     *
     * @param sort the new sort order
     * @return the filter data represented as a URL query string
     */
    public String toQueryString(String sort) {
        return String.format("?search=%s&page=%s&sort=%s&asc=%s", this.search, this.page, sort, (!sort.equals(this.sort) || !this.asc));
    }

    /* This method is called from a template, do not remove */
    public int getPaginationStart(int total) {
        int end = Math.min(page+2, total);
        return Math.max(end - 4, 1);
    }

    /* This method is called from a template, do not remove */
    public int getPaginationEnd(int total) {
        int start = Math.max(page-2, 1);
        return Math.min(start + 4, total);
    }
}
