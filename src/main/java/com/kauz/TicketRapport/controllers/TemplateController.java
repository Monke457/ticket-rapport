package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.Filter;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A controller for handling all requests pertaining to checklist templates.
 */
@Controller
public class TemplateController extends BaseController {
    @GetMapping("/checklists")
    public String getIndex(@RequestParam(required = false) UUID id,
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "description") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc,
                           Model model) {
        super.addBaseAttributes(model);

        if (id == null) {
            Filter filter = new Filter(search, sort, page, asc);
            int pageSize = 10;
            model.addAttribute("entries", unitOfWork.getChecklistTemplateService().find(ChecklistTemplate.class, filter, pageSize));
            model.addAttribute("filter", filter);
            model.addAttribute("totalPages", unitOfWork.getChecklistTemplateService().getPages(ChecklistTemplate.class, filter, pageSize));
            return "checklists/index";
        }
        model.addAttribute("entry", unitOfWork.getChecklistTemplateService().find(ChecklistTemplate.class, id));
        return "checklists/details";
    }

    @GetMapping("/checklists/items")
    public String getItems(@RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "description") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc,
                           Model model) {
        super.addBaseAttributes(model);
        Filter filter = new Filter(search, sort, page, asc);
        int pageSize = 10;
        model.addAttribute("entries", unitOfWork.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, filter, pageSize));
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", unitOfWork.getChecklistItemTemplateService().getPages(ChecklistItemTemplate.class, filter, pageSize));
        return "checklists/items/index";
    }

    @GetMapping("/checklists/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", new ChecklistTemplate());
        model.addAttribute("itemTemplates", unitOfWork.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class));
        return "checklists/create";
    }

    @RequestMapping(value = "/checklists/create", method = RequestMethod.POST)
    public String create(@RequestParam(required = false) String itemIds,
                         @RequestParam(required = false, value = "list_desc") String descriptions,
                         @ModelAttribute ChecklistTemplate entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("itemTemplates", unitOfWork.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class));
            return "checklists/create";
        }

        updateItems(entry, itemIds, descriptions);

        unitOfWork.getChecklistTemplateService().create(entry);
        return "redirect:/checklists";
    }

    @GetMapping("/checklists/edit")
    public String edit(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getChecklistTemplateService().find(ChecklistTemplate.class, id));
        model.addAttribute("itemTemplates", unitOfWork.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class));
        return "checklists/edit";
    }

    @RequestMapping(value = "/checklists/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id,
                       @RequestParam(required = false) String itemIds,
                       @RequestParam(required = false, value = "list_desc") String descriptions,
                       @ModelAttribute ChecklistTemplate entry,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("items", unitOfWork.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class));
            return "checklists/edit";
        }

        updateItems(entry, itemIds, descriptions);

        unitOfWork.getChecklistTemplateService().update(entry);
        return "redirect:/checklists";
    }

    private void updateItems(ChecklistTemplate entry, String itemIds, String descriptions) {
        Set<ChecklistItemTemplate> itemTemplates = new HashSet<>();
        if (itemIds != null && !itemIds.isBlank()) {
            Set<UUID> ids = Arrays.stream(itemIds.split(",")).map(UUID::fromString).collect(Collectors.toSet());
            itemTemplates.addAll(unitOfWork.getChecklistItemTemplateService().find(ids).collect(Collectors.toSet()));
        }
        if (descriptions != null) {
            itemTemplates.addAll(Arrays
                    .stream(descriptions.split(","))
                    .map(desc -> new ChecklistItemTemplate(desc, Set.of(entry)))
                    .collect(Collectors.toSet()));
        }
        entry.setItems(itemTemplates);
    }

    @GetMapping("/checklists/items/edit")
    public String editItems(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, id));
        return "checklists/items/edit";
    }

    @RequestMapping(value = "/checklists/items/edit", method = RequestMethod.POST)
    public String editItems(@RequestParam UUID id, @ModelAttribute ChecklistItemTemplate entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            return "checklists/items/edit";
        }
        unitOfWork.getChecklistItemTemplateService().update(entry);
        return "redirect:/checklists/items";
    }

    @GetMapping("/checklists/delete")
    public String delete(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getChecklistTemplateService().find(ChecklistTemplate.class, id));
        return "checklists/delete";
    }

    @RequestMapping(value = "/checklists/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute ChecklistTemplate entry, BindingResult result) {
        if (!result.hasErrors()) {
            unitOfWork.getChecklistTemplateService().delete(ChecklistTemplate.class, entry);
        }
        return "redirect:/checklists";
    }

    @GetMapping("/checklists/items/delete")
    public String deleteItem(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, id));
        return "checklists/items/delete";
    }

    @RequestMapping(value = "/checklists/items/delete", method = RequestMethod.POST)
    public String deleteItem(@RequestParam UUID id, @ModelAttribute ChecklistItemTemplate entry, BindingResult result) {
        if (!result.hasErrors()) {
            ChecklistItemTemplate itemTemplate = unitOfWork.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, entry.getId());
            // remove relations first
            Set<ChecklistTemplate> templates = itemTemplate.getTemplates();
            for (ChecklistTemplate template : templates) {
                template.getItems().remove(itemTemplate);
            }
            unitOfWork.getChecklistTemplateService().update(templates);
            unitOfWork.getChecklistItemTemplateService().delete(ChecklistItemTemplate.class, itemTemplate);
        }
        return "redirect:/checklists/items";
    }
}
