package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public class TicketService extends DBService<Ticket> {

    @Transactional
    public Stream<Ticket> findByLearner(User learner) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.where(cb.equal(root.get("assignedUser").get("id"), learner.getId()));

        return em.createQuery(cq).getResultStream();
    }
}
