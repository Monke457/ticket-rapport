package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * A controller for handling all requests on the homepage.
 */
@Controller
public class HomeController extends BaseController {
    @GetMapping("/")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);

        if (Objects.equals(authUser.getUser().getRole().getDescription(), "ADMIN")) {
            TicketFilter completedFilter = new TicketFilter();
            TicketFilter openFilter = new TicketFilter();

            completedFilter.setStatus("Completed");
            openFilter.setStatus("In Progress");

            model.addAttribute("completed", unitOfWork.getTicketService().find(Ticket.class, completedFilter).toList());
            model.addAttribute("open", unitOfWork.getTicketService().find(Ticket.class, openFilter).toList());
        }

        if (Objects.equals(authUser.getUser().getRole().getDescription(), "LEARNER")) {
            TicketFilter openFilter = new TicketFilter();
            TicketFilter completedFilter = new TicketFilter();
            TicketFilter closedFilter = new TicketFilter();

            openFilter.setStatus("In Progress");
            openFilter.setLearnerId(authUser.getUser().getId());
            completedFilter.setStatus("Completed");
            completedFilter.setLearnerId(authUser.getUser().getId());
            closedFilter.setStatus("Closed");
            closedFilter.setLearnerId(authUser.getUser().getId());

            model.addAttribute("userTicketsOpen", unitOfWork.getTicketService().find(Ticket.class, openFilter).toList());
            model.addAttribute("userTicketsCompleted", unitOfWork.getTicketService().find(Ticket.class, completedFilter).toList());
            model.addAttribute("userTicketsClosed", unitOfWork.getTicketService().find(Ticket.class, closedFilter).toList());
        }

        model.addAttribute("filter", new TicketFilter());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class).toList());

        return "index";
    }
}
