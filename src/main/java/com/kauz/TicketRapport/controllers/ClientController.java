package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.filters.Filter;
import com.kauz.TicketRapport.filters.TicketFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * A controller for handling all requests pertaining to client data.
 */
@Controller
public class ClientController extends BaseController {

    /**
     * Get handler for the clients page.
     *
     * @param model the model containing the relevant client data.
     * @param id the id of a client, if present will display the client details, otherwise display a list of all clients.
     * @param search string to search for clients.
     * @param sort the sort order for the list.
     * @param page the current page in the list.
     * @param asc whether the sort order is ascending.
     * @return a reference point for a client Thymeleaf template.
     */
    @GetMapping("/clients")
    public String getIndex(Model model,
                           @RequestParam(required = false) UUID id,
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "name") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc) {
        super.addBaseAttributes(model);

        if (id == null) {
            Filter filter = new Filter(search, sort, page, asc);
            int pageSize = 10;
            model.addAttribute("entries", DBServices.getClientService().find(Client.class, filter, pageSize));
            model.addAttribute("filter", filter);
            model.addAttribute("totalPages", DBServices.getClientService().getPages(Client.class, filter, pageSize));
            return "clients/index";
        }

        TicketFilter filter = new TicketFilter();
        filter.setClientId(id);
        filter.setStatus("In Progress,Completed");
        model.addAttribute("entry", DBServices.getClientService().find(Client.class, id));
        model.addAttribute("tickets", DBServices.getTicketService().find(Ticket.class, filter));
        filter.setStatus("Closed");
        model.addAttribute("closedCount", DBServices.getTicketService().find(Ticket.class, filter).count());
        return "clients/details";
    }

    /**
     * Get handler for the form to create a new client.
     *
     * @param model a model containing an empty client object.
     * @return a reference point for the client creation template.
     */
    @GetMapping("/clients/create")
    public String create(Model model, HttpServletRequest request) {
        super.addBaseAttributes(model, request);
        model.addAttribute("entry", new Client());
        return "clients/create";
    }

    /**
     * The post handler for creating a new client.
     * Checks if the data is valid and saves a new client in the database.
     *
     * @param entry the new client data.
     * @param result information about the data binding.
     * @param model the model containing the data.
     * @return a reference to a client template.
     */
    @RequestMapping(value = "/clients/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") Client entry,
                         BindingResult result,
                         Model model,
                         @RequestParam(required = false) String referer) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("referer", referer);
            return "clients/create";
        }
        DBServices.getClientService().create(entry);

        if(referer == null) return "redirect:/clients";
        return "redirect:" + referer;
    }

    /**
     * The get handler for the form to edit a client.
     *
     * @param model the model containing the client data.
     * @param id the id of the client.
     * @return a reference to the client edit template.
     */
    @GetMapping("/clients/edit")
    public String edit(Model model, @RequestParam UUID id, HttpServletRequest request) {
        super.addBaseAttributes(model, request);
        Client client = DBServices.getClientService().find(Client.class, id);
        model.addAttribute("entry", client);
        return "clients/edit";
    }

    /**
     * The post handler for editing a client.
     * Checks that the data is valid and updates the client entry in the database.
     *
     * @param id the id of the client.
     * @param entry the new data with which to update the client.
     * @param result information about the data binding.
     * @param model the model containing the data
     * @return a reference to a client template.
     */
    @RequestMapping(value = "/clients/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id,
                       @Valid @ModelAttribute("entry") Client entry,
                       BindingResult result,
                       Model model,
                       @RequestParam(required = false) String referer) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("referer", referer);
            return "clients/edit";
        }
        DBServices.getClientService().update(entry);

        if(referer == null) return "redirect:/clients";
        return "redirect:" + referer;
    }

    /**
     * The get handler for form to delete a client
     *
     * @param model the model containing the client data.
     * @param id the id of the client.
     * @return a reference to the client delete template.
     */
    @GetMapping("/clients/delete")
    public String delete(Model model, @RequestParam UUID id, HttpServletRequest request) {
        super.addBaseAttributes(model, request);
        model.addAttribute("entry", DBServices.getClientService().find(Client.class, id));
        return "clients/delete";
    }

    /**
     * The post handler to delete a client from the database.
     * First removes the client from any tickets.
     *
     * @param id the id of the client.
     * @param entry the client entry to remove.
     * @return a reference to a client template.
     */
    @RequestMapping(value = "/clients/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute Client entry, @RequestParam(required = false) String referer) {
        // remove client from tickets first
        TicketFilter filter = new TicketFilter();
        filter.setClientId(id);
        List<Ticket> clientTickets = DBServices.getTicketService().find(Ticket.class, filter).toList();
        for (Ticket ticket : clientTickets) {
            ticket.setClient(null);
        }
        DBServices.getTicketService().update(clientTickets);
        DBServices.getClientService().delete(Client.class, entry);

        if (referer == null) return "redirect:/clients";
        return "redirect:" + referer;
    }
}
