package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.*;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@Repository
public class UnitOfWork {
    @Autowired
    private UserService userService;
    @Autowired
    private DBService<Ticket> ticketService;
    @Autowired
    private DBService<Role> roleService;
    @Autowired
    private DBService<Status> statusService;
    @Autowired
    private DBService<Client> clientService;
    @Autowired
    private DBService<ChecklistItem> clItemService;
    @Autowired
    private DBService<ChecklistTemplate> clTemplateService;
    @Autowired
    private DBService<ChecklistItemTemplate> clItemTemplateService;
}
