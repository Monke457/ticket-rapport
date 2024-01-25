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

@Repository
public class ChecklistItemTemplateService extends DBService<ChecklistItemTemplate> {

    @Transactional
    public Stream<ChecklistItemTemplate> findByTemplate(UUID id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ChecklistItemTemplate> cq = cb.createQuery(ChecklistItemTemplate.class);
        Root<ChecklistItemTemplate> root = cq.from(ChecklistItemTemplate.class);

        Join<ChecklistItemTemplate, ChecklistTemplate> join = root.join("templates", JoinType.LEFT);

        cq.where(cb.equal(join.get("id"), id)).distinct(true);

        return em.createQuery(cq).getResultStream();
    }

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
