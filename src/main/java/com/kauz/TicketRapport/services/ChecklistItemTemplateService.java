package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.filters.Filter;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A repository specifically for querying checklist item template data.
 */
@Repository
public class ChecklistItemTemplateService extends DBService<ChecklistItemTemplate> {

    /**
     * A query to fetch all checklist item templates of a specific checklist template.
     * Creates a select query.
     *
     * @param id the id of the checklist template which the checklist item templates are attached to.
     * @return a stream of checklist item templates.
     */
    @Transactional
    public Stream<ChecklistItemTemplate> findByTemplate(UUID id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ChecklistItemTemplate> cq = cb.createQuery(ChecklistItemTemplate.class);
        Root<ChecklistItemTemplate> root = cq.from(ChecklistItemTemplate.class);

        Join<ChecklistItemTemplate, ChecklistTemplate> join = root.join("templates", JoinType.LEFT);

        cq.where(cb.equal(join.get("id"), id)).distinct(true);

        return em.createQuery(cq).getResultStream();
    }

    /**
     * A query to fetch checklist item templates by unique ids.
     * Creates a select query.
     *
     * @param ids a collection of unique UUID identifiers.
     * @return a stream of checklist item templates.
     */
    @Transactional
    public Stream<ChecklistItemTemplate> find(Collection<UUID> ids) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ChecklistItemTemplate> cq = cb.createQuery(ChecklistItemTemplate.class);
        Root<ChecklistItemTemplate> root = cq.from(ChecklistItemTemplate.class);

        cq.where(root.get("id").in(ids));

        return em.createQuery(cq).getResultStream();
    }

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<ChecklistItemTemplate> cq, Root<ChecklistItemTemplate> root, Filter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            cq.where(cb.like(root.get("description"), "%" + filter.getSearch() + "%"));
        }
    }
}
