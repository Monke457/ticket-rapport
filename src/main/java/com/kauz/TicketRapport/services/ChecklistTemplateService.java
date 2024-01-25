package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.filters.Filter;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * A repository specifically for querying checklist template data.
 */
@Repository
public class ChecklistTemplateService extends DBService<ChecklistTemplate> {

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<ChecklistTemplate> cq, Root<ChecklistTemplate> root, Filter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            cq.where(cb.like(root.get("description"), "%" + filter.getSearch() + "%"));
        }
    }
}
