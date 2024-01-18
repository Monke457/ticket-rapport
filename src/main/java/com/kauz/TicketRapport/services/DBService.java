package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.model.helpers.DBEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

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

        if (em.createQuery(cq).getResultStream().findAny().isEmpty()) {
            return null;
        }
        return em.createQuery(cq).getSingleResult();
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
    public void update(T entry) {
        em.merge(entry);
    }

    @Transactional
    public void delete(T entry) {
        em.remove(em.merge(entry));
    }
}
