package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.models.templates.TemplateChecklist;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * Repository for mapping template checklist data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class TemplateChecklistService extends DBService<TemplateChecklist> {
    /**
     * Adds a title filter to the query.
     * @see DBService#addFilter
     */
    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<TemplateChecklist> cq, Root<TemplateChecklist> root, Filter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            cq.where(cb.like(root.get("title"), "%" + filter.getSearch() + "%"));
        }
    }
}
