package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.ChecklistItem;
import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Status;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import com.kauz.TicketRapport.models.mappers.ChecklistMapper;
import com.kauz.TicketRapport.models.pojos.ChecklistItemPojo;
import com.kauz.TicketRapport.models.pojos.ChecklistPojo;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import jakarta.validation.Valid;
import org.hibernate.annotations.Check;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller for handling all requests pertaining to ticket data.
 */
@Controller
public class TicketController extends BaseController {
    private final ChecklistMapper mapper = new ChecklistMapper();

    /**
     * Get handler for the ticket list page. Should only be accessible by admins
     * Fetches a list of filtered and sorted ticket entries to display.
     *
     * @param model the model containing the relevant view data.
     * @param search the string to filter the tickets by.
     * @param clientId the client id to filter the tickets by.
     * @param statusId the status id to filter the tickets by.
     * @param sort a comma separated string containing the sort order.
     * @param page the current page as an integer.
     * @param asc whether the sort order is ascending.
     * @return a reference to the tickets list Thymeleaf template.
     */
    @GetMapping("/tickets")
    public String getIndex(Model model,
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "") UUID clientId,
                           @RequestParam(defaultValue = "") UUID statusId,
                           @RequestParam(defaultValue = "title") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc) {
        super.addBaseAttributes(model);

        TicketFilter filter = new TicketFilter(search, sort, page, asc, clientId, statusId);
        int pageSize = 10;
        if (!authUser.getUser().isAdmin()) {
            filter.setLearnerId(authUser.getUser().getId());
        }
        model.addAttribute("entries", DBServices.getTicketService().find(Ticket.class, filter, pageSize));
        model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
        model.addAttribute("statuses", DBServices.getStatusService().getAll(Status.class));
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", DBServices.getTicketService().getPages(Ticket.class, filter, pageSize));
        return "tickets/index";
    }

    /**
     * A get handler for the ticket details page.
     * Should be accessible by admins or the authenticated user assigned to the ticket.
     *
     * @param id the id of the ticket.
     * @param referer the view from which the user came.
     * @param model a model containing all the relevant view data.
     * @return a reference to the ticket details Thymeleaf template.
     */
    @GetMapping("/tickets/details")
    public String getDetails(@RequestParam UUID id, @RequestParam(required = false) String referer, Model model) {
        Ticket entry = DBServices.getTicketService().find(Ticket.class, id);
        if (!authUser.getUser().isAdmin()) {
            if (entry.getAssignedUser() == null || authUser.getUser().getId() != entry.getAssignedUser().getId()) {
                return referer.equals("home") ? "redirect:/" : "redirect:/tickets";
            }
        }

        super.addBaseAttributes(model);
        model.addAttribute("referer", referer);
        model.addAttribute("entry", entry);
        model.addAttribute("status", entry.getStatus().getDescription());
        return "tickets/details";
    }

    /**
     * A post handler to update the status of a ticket. Should only be available to admins.
     * Opens or closes a ticket depending on the args.
     *
     * @param id the id of the ticket to update.
     * @param action the type of update to do (close or open).
     * @param referer a string representing the page the user came from / to return to.
     * @return a reference to a Thymeleaf template.
     */
    @RequestMapping(value = "/tickets/status", method = RequestMethod.POST)
    public String status(@RequestParam UUID id, @RequestParam String action, @RequestParam(required = false) String referer) {
        String returnString = referer.equals("home") ? "redirect:/" : "redirect:/tickets";

        if (!authUser.getUser().isAdmin()) return returnString;

        Ticket ticket = DBServices.getTicketService().find(Ticket.class, id);
        if (Objects.equals(action, "close")) {
            ticket.setStatus(DBServices.getStatusService().find("Closed"));
        } else {
            ticket.setStatus(DBServices.getStatusService().find("In Progress"));
        }
        DBServices.getTicketService().update(ticket);

        return returnString;
    }

    /**
     * A get handler for a learner to update a ticket from the details page.
     * Checks that the data is valid and saves the updated ticket in the database.
     * Updates the protocol, solution, work time and checklist item status.
     * Optionally also sets the ticket status to complete.
     *
     * @param id the id of the ticket to update.
     * @param status status of the ticket as a string - hack for the status not being readable after failed update attempt.
     * @param action the type of update to do (whether to mark the ticket as completed).
     * @param checklistItems a comma separated string of ids for the checklist items that are marked as completed.
     * @param entry the ticket entry to update.
     * @param result information about the data binding.
     * @param model a model containing all the relevant view data.
     * @return a reference to a Thymeleaf template.
     */
    @RequestMapping(value = "/tickets/details/update", method = RequestMethod.POST)
    public String update(@RequestParam UUID id,
                         @RequestParam String action,
                         @RequestParam String status,
                         @RequestParam(required = false) String checklistItems,
                         @Valid @ModelAttribute("entry") Ticket entry,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            boolean error = false;
            for (ObjectError err : result.getAllErrors()) {
                if (err.getCodes() == null) continue;
                if (err.getCodes()[1].contains("protocol")) error = true;
                if (err.getCodes()[1].contains("solution")) error = true;
                if (error) break;
            }
            if (error) {
                super.addBaseAttributes(model);
                model.addAttribute("entry", entry);
                model.addAttribute("status", status);
                return "tickets/details";
            }
        }

        Ticket ticket = DBServices.getTicketService().find(Ticket.class, id);

        if (ticket.getAssignedUser() == null || ticket.getAssignedUser().getId() != authUser.getUser().getId()) {
            return "redirect:/";
        }

        if (checklistItems == null) checklistItems = "";

        ticket.setProtocol(entry.getProtocol());
        ticket.setSolution(entry.getSolution());
        ticket.setWorkHours(entry.getWorkHours());
        ticket.setWorkMinutes(entry.getWorkMinutes());
        for (ChecklistItem item : ticket.getChecklist()) {
            item.setCompleted(checklistItems.contains(item.getId().toString()));
        }
        if (action.equals("complete")) {
            ticket.setStatus(DBServices.getStatusService().find("Completed"));
        }
        DBServices.getTicketService().update(ticket);

        if (action.equals("exit")) return "redirect:/";

        return "redirect:/tickets/details?id=" + id;
    }

    @RequestMapping(value = "/tickets/details/reopen", method = RequestMethod.POST)
    public String reopen(@RequestParam UUID id,
                         @ModelAttribute Ticket entry,
                         BindingResult result) {

        Ticket ticket = DBServices.getTicketService().find(Ticket.class, id);

        if (ticket.getAssignedUser() == null || ticket.getAssignedUser().getId() != authUser.getUser().getId()) {
            return "redirect:/";
        }

        ticket.setStatus(DBServices.getStatusService().find("In Progress"));
        DBServices.getTicketService().update(ticket);

        return "redirect:/tickets/details?id=" + id;
    }

    /**
     * A get handler for the form to create a new ticket.
     *
     * @param model the model containing the relevant view data.
     * @return a reference to the Thymeleaf template of the ticket creation form.
     */
    @GetMapping("/tickets/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", new Ticket());
        model.addAttribute("users", DBServices.getUserService().getLearners());
        model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
        model.addAttribute("statuses", DBServices.getStatusService().getAll(Status.class));
        model.addAttribute("templates", DBServices.getChecklistTemplateService().getAll(ChecklistTemplate.class));
        model.addAttribute("checklistPojo", new ChecklistPojo());
        return "tickets/create";
    }

    /**
     * A post handler for creating a new ticket entry.
     * Checks that the data is valid and saves a new ticket in the database.
     * Also creates new checklist items and templates if necessary.
     *
     * @param checkboxes a string of values for the checklist items (checked or not) separated by ';;'.
     * @param descriptions a string of descriptions for the checklist items separated by ';;'.
     * @param ids a list of ids for the checklist items separated by ';;'.
     * @param entry the ticket entry to persist.
     * @param result information about the data binding.
     * @param model the model containing the relevant view data.
     * @return a reference to a Thymeleaf template.
     */
    @RequestMapping(value = "/tickets/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") Ticket entry,
                         BindingResult result,
                         @ModelAttribute("checklistPojo") ChecklistPojo checklist,
                         @RequestParam(defaultValue = "") String checkboxes,
                         @RequestParam(defaultValue = "") String descriptions,
                         @RequestParam(defaultValue = "") String ids,
                         Model model) {

        ChecklistPojo pojo = mapper.mapPojo(checklist.isSave(), checklist.getName(),
                ids.split(";;"), descriptions.split(";;"), checkboxes.split(";;"));

        validatePojo(pojo);

        if (result.hasErrors() || pojo.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", DBServices.getUserService().getLearners());
            model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
            model.addAttribute("statuses", DBServices.getStatusService().getAll(Status.class));
            model.addAttribute("templates", DBServices.getChecklistTemplateService().getAll(ChecklistTemplate.class));
            model.addAttribute("checklistPojo", pojo);
            return "tickets/create";
        }

        updateChecklist(entry, checkboxes, descriptions, ids, checklist.isSave(), checklist.getName());

        entry.setStatus(DBServices.getStatusService().find("In Progress"));

        // bug fix
        if (entry.getAssignedUser().getId() == null) {
            entry.setAssignedUser(null);
        }
        DBServices.getTicketService().create(entry);
        return "redirect:/tickets";
    }

    private void validatePojo(ChecklistPojo pojo) {
        if (pojo.getName() != null && pojo.getName().length() > 50) {
            pojo.setValid(false);
            pojo.setError("Template name may not exceed 50 characters");
        }
        for (ChecklistItemPojo item : pojo.getItems()) {
            if (item.getDescription().length() > 100) {
                item.setValid(false);
                item.setError("Description may not exceed 100 characters");
            }
        }
    }

    /**
     * A get handler to fetch checklist item templates from the database.
     * Finds items by checklist id.
     * Adds the item templates to the model and returns a fragment reference for displaying the data.
     * For adding a checklist to a ticket form from a template.
     *
     * @param id the id of the template entry.
     * @param model the model containing the relevant view data (a stream of checklist item templates).
     * @return a reference to the checklist Thymeleaf fragment.
     */
    @GetMapping("/tickets/getTemplate")
    public String checklistTemplate(@RequestParam("id") UUID id, Model model) {
        model.addAttribute("items", DBServices.getChecklistItemTemplateService().findByTemplate(id));
        return "fragments/checklist :: checklist";
    }

    /**
     * A get handler for the form to edit a ticket.
     *
     * @param model the model containing the relevant view data.
     * @param id the id of the ticket to edit.
     * @param referer a string representing the page the user came from / to return to.
     * @return a reference to the ticket edit Thymeleaf template.
     */
    @GetMapping("/tickets/edit")
    public String edit(Model model, @RequestParam UUID id, @RequestParam(required = false) String referer) {
        super.addBaseAttributes(model);
        Ticket ticket = DBServices.getTicketService().find(Ticket.class, id);
        model.addAttribute("entry", ticket);
        model.addAttribute("users", DBServices.getUserService().getLearners());
        model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
        model.addAttribute("statuses", DBServices.getStatusService().getAll(Status.class));
        model.addAttribute("checklistPojo", new ChecklistPojo());
        model.addAttribute("referer", referer);
        return "tickets/edit";
    }

    /**
     * A post handler for editing a ticket. Should only be available to admins.
     * Checks that the data is valid and updates the ticket in the database.
     * Also updates the checklist items and if necessary creates new checklist item entries.
     *
     * @param id the id of the ticket to update.
     * @param checkboxes a string of values for the checklist items (checked or not) separated by ';;'.
     * @param descriptions a string of descriptions for the checklist items separated by ';;'.
     * @param ids a list of ids for the checklist items separated by ';;'.
     * @param referer a string representing the page the user came from / to return to.
     * @param entry the new ticket data.
     * @param result information about the data binding.
     * @param model the model containing the relevant view data.
     * @return a reference to a Thymeleaf template.
     */
    @RequestMapping(value = "/tickets/edit", method = RequestMethod.POST)
    public String edit(@Valid @ModelAttribute("entry") Ticket entry,
                       BindingResult result,
                       @ModelAttribute("checklistPojo") ChecklistPojo checklist,
                       @RequestParam UUID id,
                       @RequestParam String checkboxes,
                       @RequestParam String descriptions,
                       @RequestParam String ids,
                       @RequestParam String referer,
                       Model model) {

        if (!authUser.getUser().isAdmin()) return "redirect:/tickets";

        ChecklistPojo pojo = mapper.mapPojo(checklist.isSave(), checklist.getName(),
                ids.split(";;"), descriptions.split(";;"), checkboxes.split(";;"));

        validatePojo(pojo);

        if (result.hasErrors() || pojo.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", DBServices.getUserService().getLearners());
            model.addAttribute("clients", DBServices.getClientService().getAll(Client.class));
            model.addAttribute("statuses", DBServices.getStatusService().getAll(Status.class));
            model.addAttribute("checklistPojo", pojo);
            return "tickets/edit";
        }

        updateChecklist(entry, checkboxes, descriptions, ids);
        // bug fix
        if (entry.getAssignedUser().getId() == null) {
            entry.setAssignedUser(null);
        }
        DBServices.getTicketService().update(entry);

        if (referer.equals("home")) return "redirect:/";
        return "redirect:/tickets";
    }

    private void updateChecklist(Ticket entry, String checkboxes, String descriptions, String ids) {
        updateChecklist(entry, checkboxes, descriptions, ids, false, null);
    }

    /**
     * Updates the ticket checklist items and optionally saves the new checklist as a template.
     *
     * @param entry the ticket to update.
     * @param checkboxes a string of values for the checklist items (checked or not) separated by ';;'.
     * @param descriptions a string of descriptions for the checklist items separated by ';;'.
     * @param ids a list of ids for the checklist items separated by ';;'.
     * @param saveTemplate a boolean check for if a template of the checklist should be saved.
     * @param templateName a string representing the name of the checklist template should it be saved.
     */
    private void updateChecklist(Ticket entry, String checkboxes, String descriptions, String ids, boolean saveTemplate, String templateName) {
        String[] checkArray = checkboxes.split(";;");
        String[] descArray = descriptions.split(";;");
        String[] idArray = ids.split(";;");

        Set<ChecklistItem> originalItems = DBServices.getChecklistItemService().findByTicket(entry.getId()).collect(Collectors.toSet());
        ChecklistTemplate template = new ChecklistTemplate();
        if (saveTemplate) {
            if (templateName.isBlank()) templateName = "New Template";
            template.setDescription(templateName);
            DBServices.getChecklistTemplateService().create(template);
        }

        entry.setChecklist(new HashSet<>());

        int position = 1;
        for (int i = 0; i < idArray.length; i++) {
            if (descArray[i].trim().isBlank()) continue;
            ChecklistItem item = null;

            if (idArray[i].length() > 3) {
                item = DBServices.getChecklistItemService().find(ChecklistItem.class, UUID.fromString(idArray[i]));
            }
            if (item == null) {
                item = new ChecklistItem();
            } else {
                originalItems.remove(item);
            }

            item.setDescription(descArray[i].trim());
            item.setPosition(position++);
            item.setTicket(entry);
            item.setCompleted(checkArray[i].equals("true"));

            if (saveTemplate) {
                ChecklistItemTemplate itemTemplate = null;
                if (idArray[i].length() > 3) {
                     itemTemplate = DBServices.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, UUID.fromString(idArray[i]));
                }
                if (itemTemplate == null || !itemTemplate.getDescription().trim().equals(descArray[i].trim())) {
                    itemTemplate = new ChecklistItemTemplate();
                }
                itemTemplate.getTemplates().add(template);
                itemTemplate.setDescription(descArray[i]);
                template.getItems().add(itemTemplate);
            }

            entry.getChecklist().add(item);
        }

        // delete removed items
        DBServices.getChecklistItemService().delete(ChecklistItem.class, originalItems);

        if (saveTemplate) {
            DBServices.getChecklistTemplateService().update(template);
        }
    }

    /**
     * A get handler for the form to delete a ticket entry.
     *
     * @param model the model containing the relevant view data.
     * @param id the id of the ticket to delete.
     * @return a reference to the ticket delete Thymeleaf template.
     */
    @GetMapping("/tickets/delete")
    public String delete(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", DBServices.getTicketService().find(Ticket.class, id));
        return "tickets/delete";
    }

    /**
     * A post handler for deleting a ticket entry. Should only be available to admins
     * Checks that the data is valid and deletes the ticket from the database.
     * @param id the id of the ticket to delete.
     * @param entry the ticket entry to remove.
     * @param result information about the data binding.
     * @return a reference to the ticket list Thymeleaf template.
     */
    @RequestMapping(value = "/tickets/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute Ticket entry, BindingResult result) {
        if (!result.hasErrors() && authUser.getUser().isAdmin()) {
            DBServices.getTicketService().delete(Ticket.class, entry);
        }
        return "redirect:/tickets";
    }
}
