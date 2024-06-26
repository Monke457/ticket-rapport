package ch.kauz.ticketrapport.controllers;

import ch.kauz.ticketrapport.controllers.base.BaseController;
import ch.kauz.ticketrapport.dtos.TicketDTO;
import ch.kauz.ticketrapport.filters.TicketFilter;
import ch.kauz.ticketrapport.models.Client;
import ch.kauz.ticketrapport.models.Ticket;
import ch.kauz.ticketrapport.models.User;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import ch.kauz.ticketrapport.services.base.DBServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * A controller for handling homepage and archive requests.
 */
@Controller
public class HomeController extends BaseController {

    @Autowired
    private DBServices services;

    /**
     * Get handler for the homepage.
     *
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/")
    public String getIndex(Model model) {
        addBaseAttributes(model);

        User user = authUser.getUser();

        if (user == null) {
            return "index";
        }

        if (user.isAdmin()) {
            model.addAttribute("unassigned", services.getTicketService().findUnassigned().map(TicketDTO::ofEntity).toList());
            model.addAttribute("open", services.getTicketService().findByStatus(StatusType.OPEN, true).map(TicketDTO::ofEntity).toList());
            model.addAttribute("completed", services.getTicketService().findByStatus(StatusType.COMPLETED, true).map(TicketDTO::ofEntity).toList());

        } else {
            model.addAttribute("open", services.getTicketService().findByLearnerId(user.getId(), StatusType.OPEN).map(TicketDTO::ofEntity).toList());
            model.addAttribute("completed", services.getTicketService().findByLearnerId(user.getId(), StatusType.COMPLETED).map(TicketDTO::ofEntity).toList());
            model.addAttribute("closed", services.getTicketService().findByLearnerId(user.getId(), StatusType.CLOSED).map(TicketDTO::ofEntity).toList());
        }

        return "index";
    }

    /**
     * Get handler for the archive page.
     *
     * @param filter the filter containing search and pagination information
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/archive")
    public String getArchive(@ModelAttribute("filter") TicketFilter filter, Model model) {
        addBaseAttributes(model);

        filter.setStatus(StatusType.CLOSED.getDisplay());
        filter.setPageSize(24);

        model.addAttribute("tickets", services.getTicketService().find(Ticket.class, filter).map(TicketDTO::ofEntity).toList());
        model.addAttribute("clients", services.getClientService().getAll(Client.class));
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", services.getTicketService().getPages(Ticket.class, filter));
        return "archive";
    }
}
