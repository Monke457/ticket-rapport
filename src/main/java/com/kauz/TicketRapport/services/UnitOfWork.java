package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.*;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
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
    private DBService<Client> clientService;
    @Autowired
    private DBService<ChecklistItem> checklistItemService;
    @Autowired
    private DBService<ChecklistTemplate> checklistTemplateService;
    @Autowired
    private DBService<ChecklistItemTemplate> checklistItemTemplateService;
}
