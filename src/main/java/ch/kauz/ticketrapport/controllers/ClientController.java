package ch.kauz.ticketrapport.controllers;

import ch.kauz.ticketrapport.controllers.base.BaseController;
import ch.kauz.ticketrapport.dtos.TicketDTO;
import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.models.Client;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import jakarta.validation.Valid;
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
     * @param filter the filter containing search, sort and pagination information
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/clients")
    public String getIndex(@ModelAttribute("filter") Filter filter, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entries", services.getClientService().find(Client.class, filter).toList());
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", services.getClientService().getPages(Client.class, filter));
        return "clients/index";
    }

    /**
     * Get handler for the client details page.
     *
     * @param id the id of the client to display
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/clients/details")
    public String getDetails(@RequestParam UUID id, Model model) {
        Client entry = services.getClientService().get(Client.class, id);

        addBaseAttributes(model);
        model.addAttribute("entry", entry);
        model.addAttribute("openTickets", entry.getTickets().stream()
                .filter(t -> !t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .map(TicketDTO::ofEntity).toList());
        model.addAttribute("closedTickets", entry.getTickets().stream()
                .filter(t -> t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .count());
        return "clients/details";
    }

    /**
     * Get handler for the client create page.
     *
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/clients/create")
    public String getCreate(@RequestParam(defaultValue = "1") int returnValue, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("entry", new Client());
        return "clients/create";
    }

    /**
     * Post handler for the client create request.
     * <p>
     *     Creates a new client and persists it in the database.
     * </p>
     *
     * @param entry a {@link Client} object containing the values for the new entry
     * @param result a {@link BindingResult} object containing any validation errors pertaining to the new values
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/clients/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") Client entry, BindingResult result,
                         @RequestParam int returnValue, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("entry", entry);
            return "clients/create";
        }

        services.getClientService().create(entry);
        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the client edit page.
     *
     * @param id the id of the {@link Client} to edit
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/clients/edit")
    public String getEdit(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue, @RequestParam(required = false) String referer, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("entry", services.getClientService().get(Client.class, id));
        model.addAttribute("referer", referer);
        return "clients/edit";
    }

    /**
     * Post handler for the client edit request.
     * <p>
     *     Updates a client entry in the database.
     * </p>
     *
     * @param entry a {@link Client} object containing updated values
     * @param result a {@link BindingResult} object containing any validation errors pertaining to the updated values
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/clients/edit", method = RequestMethod.POST)
    public String edit(@Valid @ModelAttribute("entry") Client entry, BindingResult result,
                       @RequestParam int returnValue, @RequestParam(required = false) String referer,
                       Model model) {

        if (result.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("entry", entry);
            model.addAttribute("referer", referer);
            return "clients/edit";
        }

        services.getClientService().update(entry);

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the client delete page.
     *
     * @param id the id of the {@link Client} to delete
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/clients/delete")
    public String getDelete(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue,
                            @RequestParam(defaultValue = "") String referer, Model model) {
        Client entry = services.getClientService().get(Client.class, id);

        addBaseAttributes(model, returnValue);
        model.addAttribute("redirectValue", getRedirectValue(returnValue, referer));
        model.addAttribute("entry", entry);
        model.addAttribute("openTickets", entry.getTickets().stream()
                .filter(t -> !t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .map(TicketDTO::ofEntity).toList());
        model.addAttribute("closedTickets", entry.getTickets().stream()
                .filter(t -> t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .count());
        return "clients/delete";
    }

    /**
     * Post handler for the client delete request.
     * <p>
     *     Removes a client entry permanently from the database.
     * </p>
     *
     * @param id the id of the {@link Client} to delete
     * @param redirectValue how far back through the browser history we need to go in order to reach a meaningful page
     * @param model the model to store the relevant data
     * @return a reference point to a redirect script
     */
    @RequestMapping(value = "/clients/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @RequestParam(defaultValue = "0") Integer redirectValue, Model model) {
        services.getClientService().delete(Client.class, id);
        model.addAttribute("returnValue", redirectValue + 2);
        return "back";
    }
}
