package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Role;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A central repository from which all other repositories can be accessed.
 */
@Getter
@Setter
@Repository
public class UnitOfWork {
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private DBService<Role> roleService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ChecklistItemService checklistItemService;
    @Autowired
    private ChecklistTemplateService checklistTemplateService;
    @Autowired
    private ChecklistItemTemplateService checklistItemTemplateService;
}
