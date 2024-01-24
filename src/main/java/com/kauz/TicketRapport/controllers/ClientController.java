package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.Filter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            model.addAttribute("entries", unitOfWork.getClientService().find(Client.class, filter, pageSize));
            model.addAttribute("filter", filter);
            model.addAttribute("totalPages", unitOfWork.getClientService().getPages(Client.class, filter, pageSize));
            return "clients/index";
        }

        model.addAttribute("entry", unitOfWork.getClientService().find(Client.class, id));
        return "clients/details";
    }

    /**
     * Get handler for the form to create a new client.
     *
     * @param model a model containing an empty client object.
     * @return a reference point for the client creation template.
     */
    @GetMapping("/clients/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
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
    public String create(@ModelAttribute Client entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            return "clients/create";
        }
        unitOfWork.getClientService().create(entry);
        return "redirect:/clients";
    }

    /**
     * The get handler for the form to edit a client.
     *
     * @param model the model containing the client data.
     * @param id the id of the client.
     * @return a reference to the client edit template.
     */
    @GetMapping("/clients/edit")
    public String edit(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        Client client = unitOfWork.getClientService().find(Client.class, id);
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
    public String edit(@RequestParam UUID id, @ModelAttribute Client entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            return "clients/edit";
        }
        unitOfWork.getClientService().update(entry);
        return "redirect:/clients";
    }

    /**
     * The get handler for form to delete a client
     *
     * @param model the model containing the client data.
     * @param id the id of the client.
     * @return a reference to the client delete template.
     */
    @GetMapping("/clients/delete")
    public String delete(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getClientService().find(Client.class, id));
        return "clients/delete";
    }

    /**
     * The post handler to delete a client from the database.
     * Checks that the data is valid and removes the client from the database.
     *
     * @param id the id of the client.
     * @param entry the client entry to remove.
     * @param result information about the data binding
     * @return a reference to a client template.
     */
    @RequestMapping(value = "/clients/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute Client entry, BindingResult result) {
        if (!result.hasErrors()) {
            unitOfWork.getClientService().delete(Client.class, entry);
        }
        return "redirect:/clients";
    }
}
