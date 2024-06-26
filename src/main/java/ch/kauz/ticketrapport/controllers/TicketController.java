package ch.kauz.ticketrapport.controllers;

import ch.kauz.ticketrapport.controllers.base.BaseController;
import ch.kauz.ticketrapport.dtos.ChecklistDTO;
import ch.kauz.ticketrapport.dtos.ChecklistItemDTO;
import ch.kauz.ticketrapport.dtos.TicketDTO;
import ch.kauz.ticketrapport.dtos.TicketProtocolDTO;
import ch.kauz.ticketrapport.filters.TicketFilter;
import ch.kauz.ticketrapport.mappers.TicketMapper;
import ch.kauz.ticketrapport.models.Client;
import ch.kauz.ticketrapport.models.Status;
import ch.kauz.ticketrapport.models.Ticket;
import ch.kauz.ticketrapport.models.helpers.RoleType;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import ch.kauz.ticketrapport.models.templates.TemplateChecklist;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.models.templates.TemplateItem;
import ch.kauz.ticketrapport.validation.TicketValidator;
import jakarta.validation.Valid;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * A controller for handling all requests pertaining to ticket data.
 */
@Controller
public class TicketController extends BaseController {

    @Autowired
    private TicketValidator validator;
    @Autowired
    private TicketMapper mapper;

    /**
     * Get handler for the tickets page.
     *
     * @param filter the filter containing search, sort and pagination information
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/tickets")
    public String getIndex(@ModelAttribute("filter") TicketFilter filter, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entries", services.getTicketService().find(Ticket.class, filter).map(TicketDTO::ofEntity).toList());
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", services.getTicketService().getPages(Ticket.class, filter));
        model.addAttribute("clients", services.getClientService().getAll(Client.class));
        model.addAttribute("statuses", services.getStatusService().getAll(Status.class));
        return "tickets/index";
    }

    /**
     * Get handler for the ticket details page.
     *
     * @param id the id of the ticket to display
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/tickets/details")
    public String getDetails(@RequestParam UUID id, Model model) throws AccessDeniedException {
        Ticket ticket = services.getTicketService().get(Ticket.class, id);

        if (!authUser.getUser().isAdmin()) {
            if (!authUser.getUser().equals(ticket.getLearner())) {
                throw new AccessDeniedException("");
            }
            if (ticket.getStatus().getDescription().equals(StatusType.OPEN.getDisplay())) {
                model.addAttribute("protocol", TicketProtocolDTO.ofEntity(ticket));
            }
        }

        addBaseAttributes(model);
        model.addAttribute("entry", TicketDTO.ofEntity(ticket));
        return "tickets/details";
    }

    /**
     * Get handler for the ticket create page.
     *
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/tickets/create")
    public String getCreate(@RequestParam(defaultValue = "1") int returnValue, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("entry", new Ticket());
        model.addAttribute("checklist", new ChecklistDTO());
        model.addAttribute("learners", services.getUserService().findByRole(RoleType.LEARNER));
        model.addAttribute("clients", services.getClientService().getAll(Client.class));
        model.addAttribute("templates", services.getTemplateChecklistService().getAll(TemplateChecklist.class));
        return "tickets/create";
    }

    /**
     * Post handler for the ticket create request.
     * <p>
     *     Creates a new ticket and persists it in the database.
     * </p>
     *
     * @param entry a {@link Ticket} object containing the values for the new entry
     * @param result a {@link BindingResult} object containing any validation errors pertaining to the new values
     * @param checklist a {@link ChecklistDTO} object containing the values for the associated checklist items
     * @param checklistResult a {@link BindingResult} object containing any validation errors pertaining to the item values
     * @param checklistData a JSON Array containing all the data for the checklist items
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/tickets/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") Ticket entry, BindingResult result,
                         @Valid @ModelAttribute("checklist") ChecklistDTO checklist, BindingResult checklistResult,
                         @RequestParam(value = "checklistData", defaultValue = "") String checklistData,
                         @RequestParam int returnValue, Model model) {
        try {
            checklist.setItems(mapper.mapChecklistItemDTOs(checklistData));
        } catch (JSONException e) {
            return "redirect:/error?code=420";
        }

        validator.validateChecklist(checklist, checklistResult);

        if (result.hasErrors() || checklistResult.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("entry", entry);
            model.addAttribute("checklist", checklist);
            model.addAttribute("learners", services.getUserService().findByRole(RoleType.LEARNER));
            model.addAttribute("clients", services.getClientService().getAll(Client.class));
            model.addAttribute("templates", services.getTemplateChecklistService().getAll(TemplateChecklist.class));
            return "tickets/create";
        }

        if (entry.getLearner().getId() == null) {
            entry.setLearner(null);
        }
        if (entry.getClient().getId() == null) {
            entry.setClient(null);
        }

        entry.setChecklistItems(mapper.mapChecklistItems(entry, checklist.getItems()));

        entry.setStatus(services.getStatusService().findByType(StatusType.OPEN));
        services.getTicketService().create(entry);

        if (checklist.isSaveAsTemplate()) {
            createTemplate(checklist);
        }

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Creates a new template checklist and template items from the checklist data transfer object values.
     *
     * @param checklist th {@link ChecklistDTO} object containing the information about the checklist
     */
    private void createTemplate(ChecklistDTO checklist) {
        TemplateChecklist templateChecklist = new TemplateChecklist(checklist.getTemplateName(), new HashSet<>());
        List<TemplateChecklistItems> checklistItems = new ArrayList<>();

        for (ChecklistItemDTO item : checklist.getItems()) {
            TemplateItem i = null;
            // look for template item in db first
            if (item.getTemplateItemId() != null) {
                i = services.getTemplateItemService().get(TemplateItem.class, item.getId());
            }
            // not a db entry, create new item (maybe the entry was only just deleted)
            if (i == null) {
                i = new TemplateItem(item.getDescription(), new HashSet<>());
            }
            checklistItems.add(new TemplateChecklistItems(templateChecklist, i, item.getOrdinal()));
        }
        services.getTemplateChecklistService().create(templateChecklist);
        services.getTemplateChecklistItemsService().create(checklistItems);
    }

    /**
     * Get handler for the ticket edit page.
     *
     * @param id the id of the {@link Ticket} to edit
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/tickets/edit")
    public String getEdit(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue,
                          @RequestParam(required = false) String referer, Model model) {
        Ticket entry = services.getTicketService().get(Ticket.class, id);

        addBaseAttributes(model, returnValue);
        model.addAttribute("referer", referer);
        model.addAttribute("entry", entry);
        model.addAttribute("checklist", ChecklistDTO.ofEntity(entry.getChecklistItems()));
        model.addAttribute("learners", services.getUserService().findByRole(RoleType.LEARNER));
        model.addAttribute("clients", services.getClientService().getAll(Client.class));
        model.addAttribute("statuses", services.getStatusService().getAll(Status.class));
        return "tickets/edit";
    }

    /**
     * Post handler for the ticket edit request.
     * <p>
     *     Updates a ticket entry in the database.
     * </p>
     *
     * @param entry a {@link Ticket} object containing updated values
     * @param result a {@link BindingResult} object containing any validation errors pertaining to the updated values
     * @param checklist a {@link ChecklistDTO} object containing the values for the associated checklist items
     * @param checklistResult a {@link BindingResult} object containing any validation errors pertaining to the item values
     * @param checklistData a JSON Array containing all the data for the checklist items
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/tickets/edit", method = RequestMethod.POST)
    public String edit(@Valid @ModelAttribute("entry") Ticket entry, BindingResult result,
                       @Valid @ModelAttribute("checklist") ChecklistDTO checklist, BindingResult checklistResult,
                       @RequestParam(value = "checklistData", defaultValue = "") String checklistData,
                       @RequestParam(required = false) String referer, @RequestParam int returnValue, Model model) {

        try {
            checklist.setItems(mapper.mapChecklistItemDTOs(checklistData));
            validator.validateChecklist(checklist, checklistResult);
        } catch (JSONException e) {
            return "redirect:/error?code=420";
        }

        if (result.hasErrors() || checklistResult.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("referer", referer);
            model.addAttribute("entry", entry);
            model.addAttribute("checklist", checklist);
            model.addAttribute("learners", services.getUserService().findByRole(RoleType.LEARNER));
            model.addAttribute("clients", services.getClientService().getAll(Client.class));
            model.addAttribute("statuses", services.getStatusService().getAll(Status.class));
            return "tickets/edit";
        }

        if (entry.getLearner().getId() == null) {
            entry.setLearner(null);
        }
        if (entry.getClient().getId() == null) {
            entry.setClient(null);
        }
        if (entry.getStatus().getId() == null) {
            entry.setStatus(null);
        }

        entry.setChecklistItems(mapper.mapChecklistItems(entry, checklist.getItems()));
        services.getTicketService().update(entry);

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the ticket delete page.
     *
     * @param id the id of the {@link Ticket} to delete
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/tickets/delete")
    public String getDelete(@RequestParam UUID id, @RequestParam(defaultValue = "1") Integer returnValue,
                            @RequestParam(defaultValue = "") String referer, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("redirectValue", getRedirectValue(returnValue, referer));
        model.addAttribute("entry", TicketDTO.ofEntity(services.getTicketService().get(Ticket.class, id)));
        return "tickets/delete";
    }

    /**
     * Post handler for the ticket delete request.
     * <p>
     *     Removes a ticket entry permanently from the database.
     * </p>
     *
     * @param id the id of the {@link Ticket} to delete
     * @param redirectValue how far back through the browser history we need to go in order to reach a meaningful page
     * @param model the model to store the relevant data
     * @return a reference point to a redirect script
     */
    @RequestMapping(value = "/tickets/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @RequestParam(defaultValue = "0") Integer redirectValue, Model model) {
        services.getTicketService().delete(Ticket.class, id);
        model.addAttribute("returnValue", redirectValue + 2);
        return "back";
    }

    /**
     * Get handler for the ticket process request. Simple redirects because this is not a valid endpoint.
     * @param id the id of the {@link Ticket} to redirect to
     * @return reference point for redirecting to the ticket details page
     */
    @GetMapping("/tickets/process")
    public String getProcess(@RequestParam UUID id) {
        return "redirect:/tickets/details?id=" + id;
    }

    /**
     * Post handler for the ticket process request.
     * <p>
     *     Sets the ticket status to closed or open depending on the submit value.
     * </p>
     *
     * @param id the id of the {@link Ticket} to process
     * @param submit the value of the submit button pressed
     * @param model the model to store the relevant data
     * @return a reference point to a redirect script
     */
    @RequestMapping(value = "/tickets/process", method = RequestMethod.POST)
    public String process(@RequestParam UUID id, @RequestParam("submit") String submit, Model model) {
        if (!authUser.getUser().isAdmin())  {
            return "redirect:/tickets/details?id=" + id;
        }

        Ticket ticket = services.getTicketService().get(Ticket.class, id);
        StatusType statusType = submit.equals("close") ? StatusType.CLOSED : StatusType.OPEN;
        ticket.setStatus(services.getStatusService().findByType(statusType));

        services.getTicketService().update(ticket);

        model.addAttribute("returnValue", 1);
        return "back";
    }

    /**
     * Get handler for the ticket protocol request. Simply redirects to the details page since this is not a valid endpoint.
     *
     * @param id the id of the {@link Ticket} to redirect to
     * @return reference point for redirecting to the ticket details page
     */
    @GetMapping("/tickets/protocol")
    public String getProtocol(@RequestParam UUID id) {
        return "redirect:/tickets/details?id=" + id;
    }

    /**
     * Post handler fot the ticket protocol request.
     * <p>
     *     Updates the ticket with the given protocol.
     *     <br>
     *     potentially marks the ticket as complete depending on the submission value.
     * </p>
     *
     * @param protocol a {@link TicketProtocolDTO} object containing the protocol values
     * @param result a {@link BindingResult} object containing any errors pertaining to the protocol values
     * @param id the id of the ticket to protocol
     * @param submit the value of the submit button pressed
     * @param model the model to store the relevant data
     * @return a reference point to a Thymeleaf template
     */
    @RequestMapping(value = "/tickets/protocol", method = RequestMethod.POST)
    public String protocol(@Valid @ModelAttribute("protocol") TicketProtocolDTO protocol, BindingResult result,
            @RequestParam UUID id, @RequestParam String submit, Model model) {
        Ticket ticket = services.getTicketService().get(Ticket.class, id);

        if (!ticket.getLearner().equals(authUser.getUser())) {
            return "redirect:/";
        }

        if (submit.equals("complete")) {
            validator.validateProtocol(protocol, result);
        }

        if (!result.hasErrors()) {
            mapper.mapProtocol(ticket, protocol);

            if (submit.equals("complete")) {
                ticket.setStatus(services.getStatusService().findByType(StatusType.COMPLETED));
            } else {
                model.addAttribute("saved", "Gespeichert: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            }

            services.getTicketService().update(ticket);
            model.addAttribute("protocol", TicketProtocolDTO.ofEntity(ticket));
        } else {
            model.addAttribute("protocol", protocol);
        }

        addBaseAttributes(model);
        model.addAttribute("entry", TicketDTO.ofEntity(ticket));
        return "tickets/details";
    }

    /**
     * Get handler for the ticket reopen request. Simply redirects to the details page since this is not a valid endpoint.
     *
     * @param id the id of the {@link Ticket} to redirect to
     * @return reference point for redirecting to the ticket details page
     */
    @GetMapping("/tickets/reopen")
    public String getReopen(@RequestParam UUID id) {
        return "redirect:/tickets/details?id=" + id;
    }

    /**
     * Post handler for the ticket reopen request.
     * <p>
     *     Sets the status to open and updates the ticket.
     * </p>
     *
     * @param id the id of the {@link Ticket} to reopen
     * @return a reference for redirecting to the ticket details page
     */
    @RequestMapping(value = "/tickets/reopen", method = RequestMethod.POST)
    public String reopen(@RequestParam UUID id) {
        Ticket ticket = services.getTicketService().get(Ticket.class, id);

        if (!ticket.getLearner().equals(authUser.getUser())) {
            return "redirect:/";
        }

        if(!ticket.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay())) {
            ticket.setStatus(services.getStatusService().findByType(StatusType.OPEN));
            services.getTicketService().update(ticket);
        }

        return "redirect:/tickets/details?id=" + id;
    }

    /**
     * Handler for creating an empty checklist item.
     * <p>
     *     Creates a new {@link ChecklistItemDTO} objects and adds it to the model.
     *     Returns a fragment to display the new item.
     * </p>
     *
     * @param model the model to store the relevant data
     * @return a reference point to a Thymeleaf fragment
     */
    @GetMapping("/tickets/getEmptyChecklistItem")
    public String getEmptyChecklistItem(@RequestParam(required = false) boolean withCheckbox, Model model) {
        model.addAttribute("item", new ChecklistItemDTO());
        model.addAttribute("withCheckbox", withCheckbox);
        return "fragments/checklists :: item-field";
    }

    /**
     * Handler for generating a checklist from a template.
     * <p>
     *     Maps template data from the database to a {@link ChecklistItemDTO} and adds it to the model.
     *     Returns a fragment to display the checklist.
     * </p>
     * @param id the id of the {@link TemplateChecklist} to find
     * @param model the model to store the relevant data
     * @return a reference point to a Thymeleaf fragment
     */
    @GetMapping("/tickets/generateChecklist")
    public String generateChecklist(@RequestParam UUID id, Model model) {
        model.addAttribute("checklist", ChecklistDTO.ofTemplateItems(services.getTemplateChecklistItemsService().findByChecklist(id).toList()));
        return "fragments/checklists :: input-field";
    }
}

