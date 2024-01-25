package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        if (authUser.getUser().isAdmin()) {
            filter.setStatus("Completed");
            model.addAttribute("completed", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
            filter.setStatus("In Progress");
        }

        if (!authUser.getUser().isAdmin()) {
            filter.setLearnerId(authUser.getUser().getId());
            filter.setStatus("In Progress");
            model.addAttribute("userTicketsOpen", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
            filter.setStatus("Completed");
            model.addAttribute("userTicketsCompleted", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
            filter.setStatus("Closed");
        }

        model.addAttribute("filter", filter);
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class).toList());
        return "index";
    }

    /**
     * A get handler for retrieving tickets according to set of filters.
     * Searches all tickets if the current user is logged in as an admin, otherwise only searches the tickets assigned tot he current user.
     *
     * @param search the string use when searching through tickets.
     * @param clientId the id of the client attached to the tickets.
     * @param status the status of the tickets.
     * @param model the model containing all the filtered tickets to populate the returned fragment.
     * @return a reference to a Thymeleaf fragment for displaying tickets as cards.
     */
    @GetMapping("/filter")
    public String filter(@RequestParam(defaultValue = "") String search,
                         @RequestParam(defaultValue = "") UUID clientId,
                         @RequestParam(defaultValue = "") String status,
                         Model model) {
        TicketFilter filter = new TicketFilter(search, clientId, status);
        if (!authUser.getUser().isAdmin()) {
            filter.setLearnerId(authUser.getUser().getId());
        }
        model.addAttribute("tickets", unitOfWork.getTicketService().find(Ticket.class, filter).toList());
        return "fragments/ticket-cards :: ticket-cards";

    }
}
