package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Status;
import com.kauz.TicketRapport.models.Ticket;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

/**
 * A controller for handling all requests pertaining to ticket data.
 */
@Controller
public class TicketController extends BaseController {
    @GetMapping("/tickets")
    public String getIndex(Model model, @RequestParam(required = false) UUID id) {
        super.addBaseAttributes(model);
        if (id != null) {
            if (Objects.equals(authUser.getUser().getRole().getDescription(), "ADMIN")) {
                model.addAttribute("entry", unitOfWork.getTicketService().find(Ticket.class, id));
            } else {
                model.addAttribute("entry", unitOfWork.getTicketService().find(Ticket.class, id));
            }
            return "tickets/details";
        }

        if (Objects.equals(authUser.getUser().getRole().getDescription(), "ADMIN")) {
            model.addAttribute("entries", unitOfWork.getTicketService().getAll(Ticket.class));
        } else {
            model.addAttribute("entries", unitOfWork.getTicketService().findByLearner(authUser.getUser()));
        }
        return "tickets/index";
    }

    @RequestMapping(value = "/tickets/status", method = RequestMethod.POST)
    public String status(@RequestParam UUID id, @RequestParam String action, Model model) {
        Ticket ticket = unitOfWork.getTicketService().find(Ticket.class, id);

        if (Objects.equals(action, "close")) {
            ticket.setStatus(unitOfWork.getStatusService().find("Closed"));
        } else {
            ticket.setStatus(unitOfWork.getStatusService().find("In Progress"));
        }
        unitOfWork.getTicketService().update(ticket);
        return "redirect:/tickets";
    }

    @RequestMapping(value = "/tickets/update", method = RequestMethod.POST)
    public String update(@RequestParam UUID id,
                         @RequestParam String protocol,
                         @RequestParam String solution,
                         @RequestParam int workHours,
                         @RequestParam int workMinutes,
                         @RequestParam String action,
                         Model model) {

        Ticket ticket = unitOfWork.getTicketService().find(Ticket.class, id);

        if (ticket.getAssignedUser().getId() != authUser.getUser().getId()) {
            return "redirect:/tickets";
        }

        ticket.setProtocol(protocol);
        ticket.setSolution(solution);
        ticket.setWorkHours(workHours);
        ticket.setWorkMinutes(workMinutes);
        if (Objects.equals(action, "complete")) {
            ticket.setStatus(unitOfWork.getStatusService().find("Complete"));
        }
        unitOfWork.getTicketService().update(ticket);

        if (Objects.equals(action, "exit")) {
            return "redirect:/tickets";
        }

        super.addBaseAttributes(model);
        model.addAttribute("entry", ticket);
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
        model.addAttribute("entry", ticket);
        model.addAttribute("users", unitOfWork.getUserService().getLearners());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
        model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
        return "tickets/edit";
    }

    @RequestMapping(value = "/tickets/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id, @ModelAttribute Ticket entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", unitOfWork.getUserService().getLearners());
            model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
            model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
            return "tickets/edit";
        }
        unitOfWork.getTicketService().update(entry);
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
