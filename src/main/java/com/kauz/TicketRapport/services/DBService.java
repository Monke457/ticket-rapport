package com.kauz.TicketRapport.services;

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
 * Base ORM for DBEntity classes. Interfaces with the database using the
 * Hibernate EntityManager, CriteriaBuilder and CriteriaQuery.
 * Uses the PersistenceContext and Transactional annotations to prevent consistency errors.
 * Any entity specific controller should extend here to inherit the relevant transaction methods.
 *
 * @param <T> any model that implements the DBEntity interface.
 */
@Repository
public class DBService<T extends DBEntity> {

    @PersistenceContext
    protected EntityManager em;

    /**
     * A query to fetch all database entries of a given entity type.
     * Creates a select query.
     *
     * @param type the type of entity to fetch.
     * @return a stream of all entries of the given type that exist in the database.
     */
    @Transactional
    public Stream<T> getAll(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        cq.from(type);

        return em.createQuery(cq).getResultStream();
    }

    /**
     * A query to fetch a single database entry based on entity type and unique identifier.
     * Creates a select query.
     *
     * @param type the entity type query.
     * @param id a unique UUID identifier.
     * @return a single entry of the given type.
     */
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

    /**
     * A query to find database entries of a given entity type based on set of filters.
     * Orders the query results based on additional filter attributes.
     * creates a select query.
     *
     * @param type the entity type to query.
     * @param filter a filter class containing all filter and sorting data.
     * @return a sorted and filtered stream of entries of the given type.
     */
    @Transactional
    public Stream<T> find(Class<T> type, Filter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);
        sortQuery(cb, cq, root, filter.getSort(), filter.isAsc());

        return em.createQuery(cq).getResultStream();
    }

    /**
     * A query to find database entries of a given entity type based on set of filters.
     * Orders and paginates the query results based on additional filter attributes.
     * Creates a select query.
     *
     * @param type the entity type to query.
     * @param filter a filter class containing all filter, sorting and pagination data.
     * @param pageSize the amount of entries to return per query.
     * @return a sorted, filtered and paginated stream of entries of the given type.
     */
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

    /**
     * Applies a filter to a query.
     * Is not generalized and should therefore be overridden and augmented by any service class that inherits from here.
     *
     * @param cb the CriteriaBuilder with which to define the predicates.
     * @param cq the CriteriaQuery with which to build the query.
     * @param root the root from query.
     * @param filter a filter object containing the relevant data.
     */
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, Filter filter) {
    }

    /**
     * Applies a sort order to a query.
     *
     * @param cb the CriteriaBuilder with which to define the predicates.
     * @param cq the CriteriaQuery with which to build the query.
     * @param root the root from query.
     * @param sort the sort order(s) as a string,
     *             multiple sort orders are separated by a comma (field1,field2),
     *             relational fields are separated with a period (join.field).
     * @param asc the sort direction.
     */
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
                    orders.add(asc
                            ? cb.asc(root.get(order))
                            : cb.desc(root.get(order))
                    );
                }
            }
            cq.orderBy(orders);
        }
    }

    /**
     * A query to find the total number of pages when the data in a table is paginated.
     * For use in displaying the page numbers.
     *
     * @param type the type of entity to query.
     * @param filter a filter object containing the same filter and pagination data that will be used to fetch data.
     * @param pageSize the amount of entries to display per page.
     * @return the number of pages needed to display all the data.
     */
    @Transactional
    public long getPages(Class<T> type, Filter filter, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);

        return (long) Math.ceil((double)em.createQuery(cq).getResultStream().count() / pageSize);
    }

    /**
     * A query to check if any entry of a given type exists in the database.
     *
     * @param type the type of entity to query.
     * @return true if at least one entry exists, false if no entries exist.
     */
    @Transactional
    public boolean anyExists(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        cq.from(type);

        return em.createQuery(cq).getResultStream().findAny().isPresent();
    }

    /**
     * A query to save a new entry in the database.
     * Creates an insert query.
     *
     * @param entry the entry to persist in the database.
     */
    @Transactional
    public void create(T entry) {
        em.persist(entry);
    }

    /**
     * A query to save multiple new entries in the database.
     * Creates multiple insert queries.
     *
     * @param entries a collection of entries to persist.
     */
    @Transactional
    public void create(Collection<T> entries) {
        for (T entry : entries) {
            em.persist(entry);
        }
    }

    /**
     * A query to update a database entry.
     * Creates an update query.
     *
     * @param entry the entry to update.
     */
    @Transactional
    public void update(T entry) {
        em.merge(entry);
    }

    /**
     * A query to update multiple database entries.
     * Creates multiple update queries.
     *
     * @param entries a collection of the entries to update.
     */
    @Transactional
    public void update(Collection<T> entries) {
        for (T entry : entries) {
            em.merge(entry);
        }
    }

    /**
     * A query to delete a database entry of a given type.
     * In order to fix a foreign key constraint issue in many-to-many relations,
     * it first fetches the entry from the database with a select query, then removes it.
     *
     * @param type the type of entity to query.
     * @param entry the entry to remove.
     */
    @Transactional
    public void delete(Class<T> type, T entry) {
        T dbEntry = find(type, entry.getId());
        em.remove(em.merge(dbEntry));
    }

    /**
     * A query to delete multiple database entries of a given type.
     *
     * @param type the type of entity to query.
     * @param entries a collection of database entries to remove.
     */
    @Transactional
    public void delete(Class<T> type, Collection<T> entries) {
        for (T entry : entries) {
            delete(type, entry);
        }
    }
}
