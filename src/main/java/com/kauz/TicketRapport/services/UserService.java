package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class UserService extends DBService<User> {

    @Transactional
    public User findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.where(cb.equal(root.get("email"), email));

        if (em.createQuery(cq).getResultStream().findAny().isEmpty()) return null;

        return em.createQuery(cq).getSingleResult();
    }
}
