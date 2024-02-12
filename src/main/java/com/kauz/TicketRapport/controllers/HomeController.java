package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * A controller for handling all requests on the homepage.
 */
@Controller
public class HomeController extends BaseController {

    /**
     * The get handler for the home page.
     * If the user is an admin, will add all open tickets to the model.
     * If the user is a learner, will add the current user's assigned tickets in lists according to ticket status.
     *
     * @param model the model containing all the relevant data needed to populate the view.
     * @return a reference to the index Thymeleaf template.
     */
    @GetMapping("/")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);

        if (authUser.getUser() == null) return "index";

        TicketFilter filter = new TicketFilter();
        if (!authUser.getUser().isAdmin()) {
            filter.setLearnerId(authUser.getUser().getId());
        }

        filter.setStatus("Completed");
        model.addAttribute("completed", DBServices.getTicketService().find(Ticket.class, filter).toList());

        filter.setStatus("In Progress");
        List<Ticket> openTickets = DBServices.getTicketService().find(Ticket.class, filter).toList();

        if (authUser.getUser().isAdmin()) {
            model.addAttribute("open", openTickets.stream().filter(t -> t.getAssignedUser() != null).toList());
            model.addAttribute("unassigned", openTickets.stream().filter(t -> t.getAssignedUser() == null).toList());

        } else {
            model.addAttribute("open", openTickets);

            filter.setStatus("Closed");
            model.addAttribute("closed", DBServices.getTicketService().find(Ticket.class, filter).toList());
            model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
        }

        model.addAttribute("filter", filter);
        return "index";
    }

    /**
     * A get handler for the archive page.
     * Shows a list of all closed tickets. handled on a separate page for performance reasons.
     * Filters the tickets according to the given conditions.
     *
     * @param model the model containing all the relevant data needed to populate the view.
     * @param search the string to search for tickets.
     * @param clientId the id the client assigned to a ticket.
     * @return a reference to the archive Thymeleaf template.
     */
    @GetMapping("/archive")
    public String getArchive(Model model,
                             @RequestParam(defaultValue = "") String search,
                             @RequestParam(defaultValue = "") UUID clientId) {
        super.addBaseAttributes(model);
        TicketFilter filter = new TicketFilter(search, clientId, "Closed");
        model.addAttribute("tickets", DBServices.getTicketService().find(Ticket.class, filter).toList());
        model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
        model.addAttribute("filter", filter);
        return "archive";
    }

    /**
     * A get handler for filtered tickets. Returns a fragment for displaying tickets as cards.
     * Fetches a list of tickets according to the conditions in the filter.
     *
     * @param search a string with which to search the tickets.
     * @param clientId the id of the client assigned to the tickets.
     * @param model the model containing the relevant fragment data.
     * @return a reference to a Thymeleaf fragment for displaying tickets.
     */
    @GetMapping("/filter")
    public String filter(@RequestParam(defaultValue = "") String search,
                         @RequestParam(defaultValue = "") UUID clientId,
                         Model model) {
        super.addBaseAttributes(model);
        TicketFilter filter = new TicketFilter(search, clientId, "Closed");
        filter.setLearnerId(authUser.getUser().getId());
        model.addAttribute("tickets", DBServices.getTicketService().find(Ticket.class, filter).toList());
        model.addAttribute("referer", "home");
        return "fragments/ticket-cards :: ticket-cards";

    }
}
