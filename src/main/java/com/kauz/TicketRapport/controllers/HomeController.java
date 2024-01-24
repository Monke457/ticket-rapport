package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
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
            model.addAttribute("completed", unitOfWork.getTicketService().find(
                    new TicketFilter(null, null, null, "Completed")));
            model.addAttribute("open", unitOfWork.getTicketService().find(
                    new TicketFilter(null, null, null, "In Progress")));
        }
        if (Objects.equals(authUser.getUser().getRole().getDescription(), "LEARNER")) {
            model.addAttribute("userTicketsOpen", unitOfWork.getTicketService().find(
                    new TicketFilter(null, authUser.getUser().getId(), null, "In Progress, Completed")));
            model.addAttribute("userTicketsClosed", unitOfWork.getTicketService().find(
                    new TicketFilter(null, authUser.getUser().getId(), null, "Closed")));
        }

        model.addAttribute("filter", new TicketFilter());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class).toList());

        return "index";
    }
}
