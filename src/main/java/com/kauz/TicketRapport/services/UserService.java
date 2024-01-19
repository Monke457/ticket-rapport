package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.User;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

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

    public Stream<User> getLearners() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.where(cb.equal(root.get("role").get("description"), "LEARNER"));

        return em.createQuery(cq).getResultStream();
    }
}
