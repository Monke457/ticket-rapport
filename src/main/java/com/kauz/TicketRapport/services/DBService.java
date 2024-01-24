package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.Filter;
import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Base ORM for DBEntity classes. Interfaces with the database using EntityManager.
 * Uses the PersistenceContext and Transactional annotations to prevent consistency errors.
 *
 * @param <T> any model that implements the DBEntity interface.
 */
@Repository
public class DBService<T extends DBEntity> {

    @PersistenceContext
    protected EntityManager em;

    @Transactional
    public Stream<T> getAll(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        cq.from(type);

        return em.createQuery(cq).getResultStream();
    }

    @Transactional
    public T find(Class<T> type, UUID id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        cq.where(cb.equal(root.get("id"), id));

        TypedQuery<T> query = em.createQuery(cq);
        if (query.getResultStream().findAny().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }

    @Transactional
    public Stream<T> find(Class<T> type, Filter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);
        sortQuery(cb, cq, root, filter.getSort(), filter.isAsc());

        return em.createQuery(cq).getResultStream();
    }

    @Transactional
    public Stream<T> find(Class<T> type, Filter filter, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);
        sortQuery(cb, cq, root, filter.getSort(), filter.isAsc());

        return em.createQuery(cq)
                .setFirstResult((filter.getPage() - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream();
    }

    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, Filter filter) {
    }

    private void sortQuery(CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, String sort, boolean asc) {
        if (sort != null && !sort.isBlank()) {
            String[] orderStrings = sort.split(",");
            List<Order> orders = new ArrayList<>();
            for (String order : orderStrings) {
                if (order.contains(".")) {
                    String join = order.substring(0, order.indexOf("."));
                    String field = order.substring(order.indexOf(".") + 1);

                    orders.add(asc
                            ? cb.asc(root.join(join, JoinType.LEFT).get(field))
                            : cb.desc(root.join(join, JoinType.LEFT).get(field))
                    );
                } else {
                    orders.add(asc ? cb.asc(root.get(order)) : cb.desc(root.get(order)));
                }
            }
            cq.orderBy(orders);
        }
    }

    @Transactional
    public long getPages(Class<T> type, Filter filter, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);

        return (long) Math.ceil((double)em.createQuery(cq).getResultStream().count() / pageSize);
    }

    @Transactional
    public boolean anyExists(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        cq.from(type);

        return em.createQuery(cq).getResultStream().findAny().isPresent();
    }

    @Transactional
    public void create(T entry) {
        em.persist(entry);
    }

    @Transactional
    public void create(Collection<T> entries) {
        for (T entry : entries) {
            em.persist(entry);
        }
    }

    @Transactional
    public void update(T entry) {
        em.merge(entry);
    }

    @Transactional
    public void update(Collection<T> entries) {
        for (T entry : entries) {
            em.merge(entry);
        }
    }

    @Transactional
    public void delete(Class<T> type, T entry) {
        T dbEntry = find(type, entry.getId());
        em.remove(em.merge(dbEntry));
    }

    @Transactional
    public void delete(Class<T> type, Collection<T> entries) {
        for (T entry : entries) {
            delete(type, entry);
        }
    }
}
