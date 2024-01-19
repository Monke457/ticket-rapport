package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Status;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.helpers.TicketConverter;
import com.kauz.TicketRapport.models.helpers.TicketFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * A controller for handling all requests pertaining to ticket data.
 */
@Controller
public class TicketController extends BaseController {
    @Autowired
    private TicketConverter ticketConverter;

    @GetMapping("/tickets")
    public String getIndex(Model model, @RequestParam(required = false) UUID id) {
        super.addBaseAttributes(model);
        if (id == null) {
            model.addAttribute("entries", unitOfWork.getTicketService().getAll(Ticket.class));
            return "tickets/index";
        }
        model.addAttribute("entry", unitOfWork.getTicketService().find(Ticket.class, id));
        return "tickets/details";
    }

    @GetMapping("/tickets/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", new Ticket());
        model.addAttribute("users", unitOfWork.getUserService().getLearners());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
        model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
        return "tickets/create";
    }

    @RequestMapping(value = "/tickets/create", method = RequestMethod.POST)
    public String create(@ModelAttribute Ticket entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", unitOfWork.getUserService().getLearners());
            model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
            model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
            return "tickets/create";
        }
        unitOfWork.getTicketService().create(entry);
        return "redirect:/tickets";
    }


    @GetMapping("/tickets/edit")
    public String edit(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        Ticket ticket = unitOfWork.getTicketService().find(Ticket.class, id);
        model.addAttribute("entry", ticketConverter.convert(ticket));
        model.addAttribute("users", unitOfWork.getUserService().getLearners());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
        model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
        return "tickets/edit";
    }

    @RequestMapping(value = "/tickets/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id, @ModelAttribute TicketFormData entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", unitOfWork.getUserService().getLearners());
            model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
            model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
            return "tickets/edit";
        }
        unitOfWork.getTicketService().update(ticketConverter.convert(entry));
        return "redirect:/tickets";
    }

    @GetMapping("/tickets/delete")
    public String delete(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getTicketService().find(Ticket.class, id));
        return "tickets/delete";
    }

    @RequestMapping(value = "/tickets/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute Ticket entry, BindingResult result) {
        if (!result.hasErrors()) {
            unitOfWork.getTicketService().delete(entry);
        }
        return "redirect:/tickets";
    }
}
