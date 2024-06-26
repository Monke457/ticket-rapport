package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Repository for mapping template checklist items data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class TemplateChecklistItemsService extends DBService<TemplateChecklistItems> {

    /**
     * Selects entries by checklist id.
     *
     * @param checklistId the {@link UUID} of the checklist
     * @return a stream of {@link TemplateChecklistItems} mapped from the database
     */
    @Transactional
    public Stream<TemplateChecklistItems> findByChecklist(UUID checklistId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TemplateChecklistItems> cq = cb.createQuery(TemplateChecklistItems.class);
        Root<TemplateChecklistItems> root = cq.from(TemplateChecklistItems.class);

        cq.where(cb.equal(root.get("checklist").get("id"), checklistId));

        return em.createQuery(cq).getResultStream();
    }

    /**
     * Removes all {@link TemplateChecklistItems} entries from the database by checklist id.
     *
     * <p>
     *     This should be used when updating TemplateChecklist entries.
     *     The old items relations are completely replaced by new ones.
     * </p>
     * <p>
     *     TODO: improve by only removing item relation entries when the item is removed from the checklist
     * </p>
     * @param checklistId the id of the checklist to query
     */
    @Transactional
    public void removeByChecklist(UUID checklistId) {
       findByChecklist(checklistId).forEach(entry -> em.remove(entry));
    }
}
