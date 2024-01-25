package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.ChecklistItem;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * A repository specifically for querying checklist item data.
 */
@Repository
public class ChecklistItemService extends DBService<ChecklistItem> {

    /**
     * A query to fetch all checklist item database entries of a specific ticket.
     * Creates a select query.
     *
     * @param ticketId the id of the ticket that the checklist items are attached to.
     * @return a stream of checklist items.
     */
    @Transactional
    public Stream<ChecklistItem> findByTicket(UUID ticketId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ChecklistItem> cq = cb.createQuery(ChecklistItem.class);
        Root<ChecklistItem> root = cq.from(ChecklistItem.class);

        cq.where(cb.equal(root.get("ticket").get("id"), ticketId));

        return em.createQuery(cq).getResultStream();
    }
}
