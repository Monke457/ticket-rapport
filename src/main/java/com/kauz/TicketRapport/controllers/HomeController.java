package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A controller for handling all requests on the homepage.
 */
@Controller
public class HomeController extends BaseController {

    /**
     * The get handler for the home page.
     * If the user is an admin, will add all the tickets marked either "completed" or "in progress" to the model.
     * If the user is a learner, will add the current user's assigned tickets in lists according to ticket status.
     *
     * @param model the model containing all the relevant data needed to populate the view.
     * @return a reference to the index Thymeleaf template.
     */
    @GetMapping("/")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);

        TicketFilter filter = new TicketFilter();
        if (!authUser.getUser().isAdmin()) {
            filter.setLearnerId(authUser.getUser().getId());
        }

        filter.setStatus("Completed");
        model.addAttribute("completed", unitOfWork.getTicketService().find(Ticket.class, filter).toList());

        filter.setStatus("In Progress");
        List<Ticket> openTickets = unitOfWork.getTicketService().find(Ticket.class, filter).toList();

        if (authUser.getUser().isAdmin()) {
            model.addAttribute("open", openTickets.stream().filter(t -> t.getAssignedUser() != null).toList());
            model.addAttribute("unassigned", openTickets.stream().filter(t -> t.getAssignedUser() == null).toList());

        } else {
            model.addAttribute("open", openTickets);

            filter.setStatus("Closed");
            model.addAttribute("closed", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
            model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
        }

        model.addAttribute("filter", filter);
        return "index";
    }

    @GetMapping("/archive")
    public String getArchive(Model model,
                             @RequestParam(defaultValue = "") String search,
                             @RequestParam(defaultValue = "") UUID clientId) {
        super.addBaseAttributes(model);
        TicketFilter filter = new TicketFilter(search, clientId, "Closed");
        model.addAttribute("tickets", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
        model.addAttribute("filter", filter);
        return "archive";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam(defaultValue = "") String search,
                         @RequestParam(defaultValue = "") UUID clientId,
                         Model model) {
        super.addBaseAttributes(model);
        TicketFilter filter = new TicketFilter(search, clientId, "Closed");
        filter.setLearnerId(authUser.getUser().getId());
        model.addAttribute("tickets", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
        model.addAttribute("referer", "home");
        return "fragments/ticket-cards :: ticket-cards";

    }
}
