package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.models.templates.TemplateChecklist;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.models.templates.TemplateItem;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Repository for mapping template item data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class TemplateItemService extends DBService<TemplateItem> {

    /**
     * Selects all database entries except those with the given ids.
     *
     * @param excludedIds a list of the ids to exclude
     * @return a stream of the selected {@link TemplateItem} entries
     */
    @Transactional
    public Stream<TemplateItem> getAllExcept(List<UUID> excludedIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TemplateItem> cq = cb.createQuery(TemplateItem.class);
        Root<TemplateItem> root = cq.from(TemplateItem.class);
        Join<TemplateItem, TemplateChecklistItems> join = root.join("checklists", JoinType.LEFT);

        cq.where(cb.not(join.get("checklist").get("id").in(excludedIds)));

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Checks if the query should be sorted by number of checklists and adds an order to sort accordingly.
     * Otherwise, sorts the query as normal.
     *
     * @param cb the {@link CriteriaBuilder} with which to build the query expressions
     * @param cq the {@link CriteriaQuery} to add the sort orders to
     * @param root the root type in the "from" clause
     * @param sort the field(s) in which to sort the query results
     * @param asc whether to sort in an ascending direction
     * @see DBService#sortQuery
     */
    @Override
    protected void sortQuery(CriteriaBuilder cb, CriteriaQuery<TemplateItem> cq, Root<TemplateItem> root, String sort, boolean asc) {
        if (sort == null || !sort.equals("checklists")) {
            super.sortQuery(cb, cq, root, sort, asc);
            return;
        }

        Join<TemplateChecklist, TemplateItem> join = root.join("checklists", JoinType.LEFT).join("checklist", JoinType.LEFT);

        cq.orderBy(asc ? cb.asc(cb.countDistinct(join.get("id"))) : cb.desc(cb.countDistinct(join.get("id"))));
        cq.groupBy(root.get("id"));
    }

    /**
     * Adds a description filter to the query.
     * @see DBService#addFilter
     */
    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<TemplateItem> cq, Root<TemplateItem> root, Filter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            cq.where(cb.like(root.get("description"), "%" + filter.getSearch() + "%"));
        }
    }
}
