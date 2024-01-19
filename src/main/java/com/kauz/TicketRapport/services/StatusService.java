package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Status;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class StatusService extends DBService<Status> {
    @Transactional
    public Status find(String value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Status> cq = cb.createQuery(Status.class);
        Root<Status> root = cq.from(Status.class);

        cq.where(cb.equal(root.get("description"), value));

        TypedQuery<Status> query = em.createQuery(cq);

        if (query.getResultStream().findAny().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }
}
