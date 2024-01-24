package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Role;
import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.models.filters.Filter;
import com.kauz.TicketRapport.models.filters.UserFilter;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A repository specifically for querying user data.
 */
@Repository
public class UserService extends DBService<User> {

    @Transactional
    public User findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.where(cb.equal(root.get("email"), email));

        TypedQuery<User> query = em.createQuery(cq);
        if (query.getResultStream().findAny().isEmpty()) return null;

        return query.getSingleResult();
    }

    @Transactional
    public Stream<User> getLearners() {
        UserFilter filter = new UserFilter();
        filter.setRole("LEARNER");
        return find(User.class, filter);
    }

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<User> cq, Root<User> root, Filter filter) {
        UserFilter f = (UserFilter) filter;
        List<Predicate> predicates = new ArrayList<>();
        Join<User, Role> roleJoin = root.join("role");

        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            predicates.add(cb.or(
                    cb.like(root.get("firstname"), "%" + f.getSearch() + "%"),
                    cb.like(root.get("lastname"), "%" + f.getSearch() + "%"),
                    cb.like(root.get("email"), "%" + f.getSearch() + "%")
            ));
        }
        if (f.getRoleId() != null) {
            predicates.add(cb.equal(roleJoin.get("id"), f.getRoleId()));
        }
        if (f.getRole() != null && !f.getRole().isBlank()) {
            predicates.add(cb.like(roleJoin.get("description"), "%" + f.getRole() + "%"));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[]{})));
    }
}
