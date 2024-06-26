package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.models.Status;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

/**
 * Repository for mapping status data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class StatusService extends DBService<Status> {

    /**
     * Selects a status database entry by description.
     *
     * @param type a {@link StatusType} enum whose display value corresponds to the description of the status to select
     * @return a {@link Status} object mapped from the selected database entry
     */
    @Transactional
    public Status findByType(StatusType type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Status> cq = cb.createQuery(Status.class);
        Root<Status> root = cq.from(Status.class);

        cq.where(cb.equal(root.get("description"), type.getDisplay()));

        TypedQuery<Status> query = em.createQuery(cq);
        if (query.getResultStream().findAny().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }
}
