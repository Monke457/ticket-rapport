package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.filters.TicketFilter;
import ch.kauz.ticketrapport.models.Ticket;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Repository for mapping ticket data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class TicketService extends DBService<Ticket> {

    /**
     * Selects tickets from the database that do not have an assigned learner.
     *
     * @return a stream of {@link Ticket} objects mapped from the selected data
     */
    @Transactional
    public Stream<Ticket> findUnassigned() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.where(cb.and(
                cb.isNull(root.get("learner")),
                cb.notEqual(root.get("status").get("description"), StatusType.CLOSED.getDisplay())
        ));

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Selects tickets from the database by status.
     *
     * @param type the {@link StatusType} value to search for
     * @param assignedOnly if true, only selects tickets that are assigned to a learner
     * @return a stream of {@link Ticket} objects mapped from the selected data
     */
    @Transactional
    public Stream<Ticket> findByStatus(StatusType type, boolean assignedOnly) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status").get("description"), type.getDisplay()));

        if (assignedOnly) {
            predicates.add(cb.isNotNull(root.get("learner")));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Selects tickets from the database by assigned learner and status.
     *
     * @param learnerId the id of the assigned learner
     * @param type the {@link StatusType} value to search for
     * @return a stream of {@link Ticket} objects mapped from the selected data
     */
    @Transactional
    public Stream<Ticket> findByLearnerId(UUID learnerId, StatusType type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.where(cb.and(
                cb.equal(root.join("learner").get("id"), learnerId),
                cb.equal(root.get("status").get("description"), type.getDisplay())
        ));

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Adds filters for any combination of the following attributes:
     * <p>
     *     title, description, assigned learner's name, client id, status id, status description
     * </p>
     *
     * @param cb the {@link CriteriaBuilder} with which to build query expressions
     * @param cq the {@link CriteriaQuery} in which to include the filter expressions
     * @param root the root type in the form clause
     * @param filter a {@link Filter} containing the filter information
     */
    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<Ticket> cq, Root<Ticket> root, Filter filter) {
        TicketFilter f = (TicketFilter) filter;
        List<Predicate> predicates = new ArrayList<>();

        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            String s = "%" + f.getSearch() + "%";
            predicates.add(cb.or(
                    cb.like(root.get("title"), s),
                    cb.like(root.get("description"), s),
                    cb.like(root.get("learner").get("fullName"), s)
            ));
        }
        if (f.getClientId() != null) {
            predicates.add(cb.equal(root.get("client").get("id"), f.getClientId()));
        }
        if (f.getStatusId() != null) {
            predicates.add(cb.equal(root.get("status").get("id"), f.getStatusId()));
        }
        if (f.getStatus() != null && !f.getStatus().isBlank()) {
            predicates.add(cb.equal(root.get("status").get("description"), ((TicketFilter) filter).getStatus()));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
    }
}
