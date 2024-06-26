package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.filters.UserFilter;
import ch.kauz.ticketrapport.models.User;
import ch.kauz.ticketrapport.models.helpers.RoleType;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.TypedQuery;
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
 * Repository for mapping user data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class UserService extends DBService<User> {

    /**
     * Selects a user database entry by email.
     * <p>
     *     Should only return one object, since the email is unique.
     * </p>
     *
     * @param email the email to search for
     * @return a single {@link User} object mapped from the database
     */
    @Transactional
    public User findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.where(cb.equal(root.get("email"), email));

        TypedQuery<User> query = em.createQuery(cq);
        if (query.getResultStream().findAny().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }

    /**
     * Selects user database entries by email.
     *
     * @param roleType the {@link RoleType} value to search for
     * @return a stream of {@link User} objects mapped from the selected data
     */
    @Transactional
    public Stream<User> findByRole(RoleType roleType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.where(cb.equal(root.join("role").get("description"), roleType.getDisplay()));

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Selects a user's password from the database by id.
     * <p>
     *     This should be used when updating existing user entries in order to retain the original password.
     *     <br>
     *     Otherwise the passwords are overwritten with an empty string on update.
     * </p>
     *
     * @param id the id of the user to select
     * @return the user's password hash as a string
     */
    @Transactional
    public String getPassword(UUID id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<User> root = cq.from(User.class);

        cq.where(cb.equal(root.get("id"), id));
        cq.select(root.get("password"));

        TypedQuery<String> query = em.createQuery(cq);
        if (query.getResultStream().findAny().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }

    /**
     * Adds a filter for any combination of the following attributes:
     * <p>
     *     full name, email, role id
     * </p>
     *
     * @param cb the {@link CriteriaBuilder} with which to build query expressions
     * @param cq the {@link CriteriaQuery} in which to include the filter expressions
     * @param root the root type in the form clause
     * @param filter a {@link Filter} containing the filter information
     */
    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<User> cq, Root<User> root, Filter filter) {
        UserFilter f = (UserFilter) filter;
        List<Predicate> predicates = new ArrayList<>();

        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            String s = "%" + f.getSearch() + "%";
            predicates.add(cb.or(
                    cb.like(root.get("fullName"), s),
                    cb.like(root.get("email"), s)
            ));
        }
        if (f.getRoleId() != null) {
            predicates.add(cb.equal(root.get("role").get("id"), f.getRoleId()));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
    }
}
