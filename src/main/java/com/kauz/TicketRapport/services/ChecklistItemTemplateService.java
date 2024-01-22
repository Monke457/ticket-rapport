package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.ChecklistItem;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

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
}
