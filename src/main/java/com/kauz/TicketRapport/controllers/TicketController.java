package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.ChecklistItem;
import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Status;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A controller for handling all requests pertaining to ticket data.
 */
@Controller
public class TicketController extends BaseController {
    @GetMapping("/tickets")
    public String getIndex(Model model, @RequestParam(required = false) UUID id, @RequestParam(required = false) String referer) {
        super.addBaseAttributes(model);

        if (id != null) {
            model.addAttribute("referer", referer);
            Ticket entry = unitOfWork.getTicketService().find(Ticket.class, id);

            if (entry.getAssignedUser().getId() != authUser.getUser().getId() &&
                    !Objects.equals(authUser.getUser().getRole().getDescription(), "ADMIN")) {
                return "redirect:/tickets";
            }

            model.addAttribute("entry", entry);
            return "tickets/details";
        }

        if (Objects.equals(authUser.getUser().getRole().getDescription(), "ADMIN")) {
            model.addAttribute("entries", unitOfWork.getTicketService().getAll(Ticket.class));

        } else {
            TicketFilter filter = new TicketFilter();
            filter.setLearnerId(authUser.getUser().getId());
            model.addAttribute("entries", unitOfWork.getTicketService().find(filter));
        }
        return "tickets/index";
    }

    @RequestMapping(value = "/tickets/status", method = RequestMethod.POST)
    public String status(@RequestParam UUID id, @RequestParam String action, @RequestParam(required = false) String referer) {
        Ticket ticket = unitOfWork.getTicketService().find(Ticket.class, id);
        if (Objects.equals(action, "close")) {
            ticket.setStatus(unitOfWork.getStatusService().find("Closed"));
        } else {
            ticket.setStatus(unitOfWork.getStatusService().find("In Progress"));
        }
        unitOfWork.getTicketService().update(ticket);
        if (Objects.equals(referer, "home")) {
            return "redirect:/";
        }
        return "redirect:/tickets";
    }

    @RequestMapping(value = "/tickets/update", method = RequestMethod.POST)
    public String update(@RequestParam UUID id,
                         @RequestParam(required = false) String referer,
                         @ModelAttribute Ticket entry,
                         BindingResult result,
                         @RequestParam String action,
                         @RequestParam(required = false) String checklistItems) {

        Ticket ticket = unitOfWork.getTicketService().find(Ticket.class, id);

        if (!result.hasErrors()) {
            if (ticket.getAssignedUser().getId() != authUser.getUser().getId()) {
                if(Objects.equals(referer, "home")) return "redirect:/";
                return "redirect:/tickets";
            }
            if (checklistItems == null) checklistItems = "";

            ticket.setProtocol(entry.getProtocol());
            ticket.setSolution(entry.getSolution());
            ticket.setWorkHours(entry.getWorkHours());
            ticket.setWorkMinutes(entry.getWorkMinutes());
            for (ChecklistItem item : ticket.getChecklist()) {
                item.setCompleted(checklistItems.contains(item.getId().toString()));
            }
            if (Objects.equals(action, "complete")) {
                ticket.setStatus(unitOfWork.getStatusService().find("Completed"));
            }
            unitOfWork.getTicketService().update(ticket);

            if (Objects.equals(action, "exit")) {
                if(Objects.equals(referer, "home")) return "redirect:/";
                return "redirect:/tickets";
            }
        }
        return "redirect:/tickets?id=" + id + "&referer=" + referer;
    }

    @GetMapping("/tickets/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", new Ticket());
        model.addAttribute("users", unitOfWork.getUserService().getLearners());
        model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
        model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
        model.addAttribute("templates", unitOfWork.getChecklistTemplateService().getAll(ChecklistTemplate.class));
        return "tickets/create";
    }

    @RequestMapping(value = "/tickets/create", method = RequestMethod.POST)
    public String create(@RequestParam String checkboxes,
                         @RequestParam String descriptions,
                         @RequestParam String ids,
                         @RequestParam(required = false) String saveTemplate,
                         @RequestParam(required = false) String templateName,
                         @ModelAttribute Ticket entry,
                         BindingResult result, Model model) {

        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", unitOfWork.getUserService().getLearners());
            model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
            model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
            model.addAttribute("templates", unitOfWork.getChecklistTemplateService().getAll(ChecklistTemplate.class));
            return "tickets/create";
        }

        updateChecklist(entry, checkboxes, descriptions, ids, saveTemplate != null, templateName);

        entry.setStatus(unitOfWork.getStatusService().find("In Progress"));
        unitOfWork.getTicketService().create(entry);
        return "redirect:/tickets";
    }

    @GetMapping("/tickets/getTemplate")
    public String checklistTemplate(@RequestParam("id") UUID id, Model model) {
        model.addAttribute("items", unitOfWork.getChecklistItemTemplateService().findByTemplate(id));
        return "fragments/checklist :: checklist";
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
    public String edit(@RequestParam UUID id,
                       @RequestParam String checkboxes,
                       @RequestParam String descriptions,
                       @RequestParam String ids,
                       @ModelAttribute Ticket entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("users", unitOfWork.getUserService().getLearners());
            model.addAttribute("clients", unitOfWork.getClientService().getAll(Client.class));
            model.addAttribute("statuses", unitOfWork.getStatusService().getAll(Status.class));
            return "tickets/edit";
        }

        updateChecklist(entry, checkboxes, descriptions, ids);

        unitOfWork.getTicketService().update(entry);
        return "redirect:/tickets";
    }

    private void updateChecklist(Ticket entry, String checkboxes, String descriptions, String ids) {
        updateChecklist(entry, checkboxes, descriptions, ids, false, null);
    }

    private void updateChecklist(Ticket entry, String checkboxes, String descriptions, String ids, boolean saveTemplate, String templateName) {
        String[] checkArray = checkboxes.split(";;");
        String[] descArray = descriptions.split(";;");
        String[] idArray = ids.split(";;");

        Set<ChecklistItem> originalItems = unitOfWork.getChecklistItemService().findByTicket(entry.getId()).collect(Collectors.toSet());
        ChecklistTemplate template = new ChecklistTemplate();
        if (saveTemplate) {
            if (templateName == null) templateName = "New Template";
            template.setDescription(templateName);
            unitOfWork.getChecklistTemplateService().create(template);
        }

        entry.setChecklist(new HashSet<>());

        for (int i = 0; i < idArray.length; i++) {
            if (descArray[i].trim().isEmpty()) continue;

            ChecklistItem item = null;
            if (idArray[i].length() > 3) {
                item = unitOfWork.getChecklistItemService().find(ChecklistItem.class, UUID.fromString(idArray[i]));
            }
            if (item != null) originalItems.remove(item);
            if (item == null) item = new ChecklistItem();

            item.setDescription(descArray[i].trim());
            item.setTicket(entry);
            item.setCompleted(Objects.equals(checkArray[i], "true"));

            if (saveTemplate) {
                ChecklistItemTemplate itemTemplate = null;
                if (idArray[i].length() > 3) {
                     itemTemplate = unitOfWork.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, UUID.fromString(idArray[i]));
                }
                if (itemTemplate == null) {
                    itemTemplate = new ChecklistItemTemplate();
                }
                itemTemplate.getTemplates().add(template);
                itemTemplate.setDescription(descArray[i]);
                template.getItems().add(itemTemplate);
            }

            entry.getChecklist().add(item);
        }

        // delete removed items
        unitOfWork.getChecklistItemService().delete(ChecklistItem.class, originalItems);

        if (saveTemplate) {
            unitOfWork.getChecklistTemplateService().update(template);
        }
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
            unitOfWork.getTicketService().delete(Ticket.class, entry);
        }
        return "redirect:/tickets";
    }
}
