package ch.kauz.ticketrapport.services.base;

import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.models.base.DBEntity;
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
 * Main ORM Repository containing all query methods.
 * <p>
 *     Interfacing with the database is controlled using an atomised "Unit of Work" concept with EntityManager and PersistenceContext.
 *     <br>
 *     Queries are built with Criteria API and Hibernate. To maintain database consistency, each query is a complete transaction.
 *     <br>
 *     All model-specific repositories should inherit from this class.
 * </p>
 *
 * @param <T> a model class that implements {@link DBEntity}
 * @see DBServices
 */
@Repository
public class DBService<T extends DBEntity> {
    @PersistenceContext
    protected EntityManager em;

    /**
     * Selects a single database entry by id.
     *
     * @param type the class of model - the table - to select from
     * @param id the UUID of the entry to select
     * @return the database entry with the given id. If none is found, returns null.
     */
    @Transactional
    public T get(Class<T> type, UUID id) {
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
     * Selects all database entries from a table.
     *
     * @param type the class of model - the table - to select from
     * @return a sorted stream of all database entries of the given type
     */
    @Transactional
    public Stream<T> getAll(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        cq.from(type);

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Selects filtered database entries from a table, sorts and paginates.
     *
     * @param type the class of model - the table - to select from
     * @param filter a {@link Filter} object containing all filter, sorting, and pagination information
     * @return a stream of database entries of the given type
     */
    @Transactional
    public Stream<T> find(Class<T> type, Filter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);
        sortQuery(cb, cq, root, filter.getSort(), filter.isAsc());

        return em.createQuery(cq)
                .setFirstResult((filter.getPage() - 1) * filter.getPageSize())
                .setMaxResults(filter.getPageSize())
                .getResultStream();
    }

    /**
     * Calculates how many pages are needed to display all the database entries that a {@link #find} query would return.
     *
     * @param type the class of model - the table - to query
     * @param filter a {@link Filter} object containing the data that would be used to query the database
     * @return the amount of pages as an integer
     */
    @Transactional
    public int getPages(Class<T> type, Filter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        addFilter(cb, cq, root, filter);

        return (int) Math.ceil((double)em.createQuery(cq).getResultStream().count() / filter.getPageSize());
    }

    /**
     * Checks if an entry exists in the database of a specific table.
     *
     * @param type the class of model - the table - to query
     * @return true if at least one entry exists in the database
     */
    @Transactional
    public boolean anyExists(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        cq.from(type);

        return em.createQuery(cq).setMaxResults(1).getResultStream().findAny().isPresent();
    }

    /**
     * Persists a new entry in the database.
     * @param entry the entry to persist
     */
    @Transactional
    public void create(T entry) {
        em.persist(entry);
    }

    /**
     * Persists a collection of new entries in the database.
     * @param entries a collection of the entries to persist
     */
    @Transactional
    public void create(Collection<T> entries) {
        for (T entry : entries) {
            em.persist(entry);
        }
    }

    /**
     * Updates a database entry.
     * @param entry the entry to update
     */
    @Transactional
    public void update(T entry) {
        em.merge(entry);
    }

    /**
     * Removes an entry from the database.
     * <p>
     *     Selects the entry from the persistence context first, in order to facilitate the orphan removal and cascade remove functionality.
     * </p>
     *
     * @param type the class of model - the table - to select from
     * @param id the id of the entry to remove
     */
    @Transactional
    public void delete(Class<T> type, UUID id) {
        T entryFromDB = get(type, id);
        em.remove(entryFromDB);
    }

    /**
     * Adds sort orders to a query.
     * <p>
     *     The sort string can contain multiple fields separated by a comma.
     *     It can also contain nested field (fields of a related entity), denoted by a period.
     * </p>
     * <p>
     *     Example for sorting users:
     *     <br>
     *     <strong>"role.description,lastname,firstname"</strong>
     *     <br>
     *     The above example would sort a users alphabetically first by the description of role, then by last name, then by first name.
     * </p>
     *
     * @param cb the {@link CriteriaBuilder} with which to build the query expressions
     * @param cq the {@link CriteriaQuery} to add the sort orders to
     * @param root the root type in the "from" clause
     * @param sort the field(s) in which to sort the query results
     * @param asc whether to sort in an ascending direction
     */
    protected void sortQuery(CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, String sort, boolean asc) {
        if (sort == null || sort.isBlank()) {
            return;
        }

        String[] orderStrings = sort.split(",");
        List<Order> orders = new ArrayList<>();

        for (String order : orderStrings) {
            if (order.contains(".")) {
                String joinField = order.substring(0, order.indexOf("."));
                String field = order.substring(order.indexOf(".") + 1);

                Join<T, ?> join = root.join(joinField, JoinType.LEFT);

                // using descending to sort non-null relations first
                orders.add(asc ? cb.desc(cb.isNotNull(root.get(joinField))) : cb.asc(cb.isNotNull(root.get(joinField))));
                orders.add(asc ? cb.asc(join.get(field)) : cb.desc(join.get(field)));
            } else {
                orders.add(asc ? cb.asc(root.get(order)) : cb.desc(root.get(order)));
            }
        }
        cq.orderBy(orders);
    }

    /**
     * Default method to add a filter to a query. Since each model has different attributes to filter,
     * each model-specific repository should override this method.
     *
     * @param cb the {@link CriteriaBuilder} with which to build query expressions
     * @param cq the {@link CriteriaQuery} in which to include the filter expressions
     * @param root the root type in the form clause
     * @param filter a {@link Filter} containing the filter information
     */
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, Filter filter) {
    }
}
