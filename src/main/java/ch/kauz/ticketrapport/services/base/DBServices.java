package ch.kauz.ticketrapport.services.base;

import ch.kauz.ticketrapport.models.Role;
import ch.kauz.ticketrapport.services.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Central service class containing all model-specific repositories.
 * <p>
 *     For access to any and all repositories with a single injection.
 * </p>
 */
@Repository
@Getter
public class DBServices {
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private DBService<Role> roleService;
    @Autowired
    private TemplateChecklistService templateChecklistService;
    @Autowired
    private TemplateItemService templateItemService;
    @Autowired
    private TemplateChecklistItemsService templateChecklistItemsService;
}
