package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.filters.Filter;
import com.kauz.TicketRapport.dtos.ItemTemplateDTO;
import com.kauz.TicketRapport.mappers.TemplateMapper;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller for handling all requests pertaining to checklist templates.
 */
@Controller
public class TemplateController extends BaseController {

    private final TemplateMapper mapper = new TemplateMapper();

    /**
     * Get handler for the checklist templates page.
     *
     * @param id the id of a checklist template, if present will display details for the template, otherwise will display a list of all templates.
     * @param search string to search for checklist templates.
     * @param sort sort order for the list.
     * @param page current page in the list.
     * @param asc whether the sort order is ascending.
     * @param model the model containing data for the endpoint.
     * @return a reference to a checklist Thymeleaf template.
     */
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
            model.addAttribute("entries", DBServices.getChecklistTemplateService().find(ChecklistTemplate.class, filter, pageSize));
            model.addAttribute("filter", filter);
            model.addAttribute("totalPages", DBServices.getChecklistTemplateService().getPages(ChecklistTemplate.class, filter, pageSize));
            return "checklists/index";
        }
        model.addAttribute("entry", DBServices.getChecklistTemplateService().find(ChecklistTemplate.class, id));
        return "checklists/details";
    }

    /**
     * Get handler for the checklist item templates page.
     * returns a list of all templates.
     *
     * @param search string to search for item templates.
     * @param sort sort order for the list.
     * @param page the current page in the list.
     * @param asc whether the sort order is ascending.
     * @param model the model containing data for the endpoint.
     * @return a reference to the checklist items Thymeleaf template.
     */
    @GetMapping("/checklists/items")
    public String getItems(@RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "description") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc,
                           Model model) {
        super.addBaseAttributes(model);
        Filter filter = new Filter(search, sort, page, asc);
        int pageSize = 10;
        model.addAttribute("entries", DBServices.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, filter, pageSize));
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", DBServices.getChecklistItemTemplateService().getPages(ChecklistItemTemplate.class, filter, pageSize));
        return "checklists/items/index";
    }

    /**
     * Get handler for the form to create a new checklist template.
     *
     * @param model the model containing data for the endpoint.
     * @return a reference to the checklist create template.
     */
    @GetMapping("/checklists/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        List<ChecklistItemTemplate> itemTemplates = DBServices.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class).toList();
        List<ItemTemplateDTO> itemPojos = mapper.mapDTO(itemTemplates);

        model.addAttribute("entry", new ChecklistTemplate());
        model.addAttribute("itemPojos", itemPojos);
        return "checklists/create";
    }

    /**
     * Post handler for creating a new checklist template.
     * Checks if the data is valid and saves a new checklist template to the database.
     * Creates or updates the item templates that are contained in the checklist.
     *
     * @param entry the checklist template to save.
     * @param result information about the data binding.
     * @param model the model containing data for the endpoint.
     * @return a reference to a checklist Thymeleaf template.
     */
    @RequestMapping(value = "/checklists/create", method = RequestMethod.POST)
    public String create(@RequestParam(required = false, name = "checked_items") String checkedItems,
                         @RequestParam(required = false, name = "checked_ids") String checkedIds,
                         @Valid @ModelAttribute("entry") ChecklistTemplate entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);

            Collection<ChecklistItemTemplate> itemTemplates = DBServices.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class).toList();
            List<ItemTemplateDTO> itemPojos = mapper.mapDTO(itemTemplates, checkedItems);

            model.addAttribute("entry", entry);
            model.addAttribute("itemPojos", itemPojos);
            return "checklists/create";
        }

        updateItems(entry, checkedIds, checkedItems);

        DBServices.getChecklistTemplateService().create(entry);
        return "redirect:/checklists";
    }

    /**
     * Get handler for the form to edit a checklist template.
     *
     * @param id the id of the checklist template.
     * @param model the model containing data for the endpoint.
     * @return a reference to the checklist template edit form.
     */
    @GetMapping("/checklists/edit")
    public String edit(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);

        List<ChecklistItemTemplate> itemTemplates = DBServices.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class).toList();
        ChecklistTemplate entry = DBServices.getChecklistTemplateService().find(ChecklistTemplate.class, id);

        List<ItemTemplateDTO> itemPojos = mapper.mapDTO(itemTemplates, entry.getItems());

        model.addAttribute("entry", entry);
        model.addAttribute("itemPojos", itemPojos);

        return "checklists/edit";
    }

    /**
     * Post handler for editing a checklist template.
     * Checks that the data is valid and updated the checklist template in the database.
     * Also updates the checklist item templates connected to the checklist.
     *
     * @param id the id of the checklist template to update.
     * @param entry the checklist template entry to update with the new data.
     * @param result information about the data binding.
     * @param model the model containing data for the endpoint.
     * @return a reference to a checklist Thymeleaf template.
     */
    @RequestMapping(value = "/checklists/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id,
                       @RequestParam(required = false, name="checked_ids", defaultValue = "") String checkedIds,
                       @RequestParam(required = false, name="checked_items", defaultValue = "") String checkedItems,
                       @Valid @ModelAttribute("entry") ChecklistTemplate entry,
                       BindingResult result, Model model) {

        if (result.hasErrors()) {
            super.addBaseAttributes(model);

            Collection<ChecklistItemTemplate> itemTemplates = DBServices.getChecklistItemTemplateService().getAll(ChecklistItemTemplate.class).toList();
            List<ItemTemplateDTO> itemPojos = mapper.mapDTO(itemTemplates, checkedItems);

            model.addAttribute("entry", entry);
            model.addAttribute("itemPojos", itemPojos);
            return "checklists/edit";
        }

        updateItems(entry, checkedIds, checkedItems);

        DBServices.getChecklistTemplateService().update(entry);
        return "redirect:/checklists";
    }

    /**
     * A method to update the relational checklist item templates in a new or updated checklist template database entry.
     * Replaces the checklist item templates set with a new one populated with entries from the database.
     * Creates new checklist item templates based on the descriptions.
     *
     * @param entry the checklist template entry to update.
     * @param itemIds comma separated string of ids for the persisted checklist item templates.
     * @param descriptions comma separated string of descriptions for the new checklist item templates.
     */
    private void updateItems(ChecklistTemplate entry, String itemIds, String descriptions) {
        Set<UUID> ids = Arrays.stream(itemIds.split(","))
                .filter(id -> id.length() > 5)
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        Set<ChecklistItemTemplate> itemTemplates = DBServices.getChecklistItemTemplateService().find(ids).collect(Collectors.toSet());

        for (ChecklistItemTemplate item : itemTemplates) {
            descriptions = descriptions.replace(item.getDescription(), "");
        }

        itemTemplates.addAll(Arrays
                .stream(descriptions.split(","))
                .filter(i -> !i.isBlank())
                .map(desc -> new ChecklistItemTemplate(desc, Set.of(entry)))
                .collect(Collectors.toSet()));

        entry.setItems(itemTemplates);
    }

    /**
     * Get handler for the form to edit a checklist item template.
     *
     * @param id the id of the checklist item template entry.
     * @param model the model containing data for the endpoint.
     * @return a reference to the checklist item template edit form.
     */
    @GetMapping("/checklists/items/edit")
    public String editItems(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", DBServices.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, id));
        return "checklists/items/edit";
    }

    /**
     * Post handler for editing a checklist item template.
     * Checks that the data is valid and updates the entry.
     *
     * @param id the id of the checklist item template.
     * @param entry the new checklist item template data.
     * @param result information about the data binding.
     * @param model the model containing data for the endpoint.
     * @return a reference to a checklist item Thymeleaf template.
     */
    @RequestMapping(value = "/checklists/items/edit", method = RequestMethod.POST)
    public String editItems(@RequestParam UUID id, @Valid @ModelAttribute("entry") ChecklistItemTemplate entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            return "checklists/items/edit";
        }
        DBServices.getChecklistItemTemplateService().update(entry);
        return "redirect:/checklists/items";
    }

    /**
     * Get handler for the form to delete a checklist template.
     *
     * @param id the id of the checklist template to delete.
     * @param model the model containing data for the endpoint.
     * @return a reference to a checklist delete form.
     */
    @GetMapping("/checklists/delete")
    public String delete(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", DBServices.getChecklistTemplateService().find(ChecklistTemplate.class, id));
        return "checklists/delete";
    }

    /**
     * Post handler for deleting a checklist template.
     * Checks that the data is valid and removes the entry from the database.
     *
     * @param id the id of the checklist template to delete.
     * @param entry the checklist template database entry.
     * @param result information about the data binding.
     * @return a reference to a checklist Thymeleaf template.
     */
    @RequestMapping(value = "/checklists/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute ChecklistTemplate entry, BindingResult result) {
        if (!result.hasErrors()) {
            ChecklistTemplate template = DBServices.getChecklistTemplateService().find(ChecklistTemplate.class, id);
            // remove relations first
            template.getItems().clear();
            DBServices.getChecklistTemplateService().delete(ChecklistTemplate.class, template);
        }
        return "redirect:/checklists";
    }

    /**
     * Get handler for the form to delete a checklist item template.
     *
     * @param id the id of the checklist item template.
     * @param model the model containing data for the endpoint.
     * @return a reference to a checklist item delete form.
     */
    @GetMapping("/checklists/items/delete")
    public String deleteItem(@RequestParam UUID id, Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", DBServices.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, id));
        return "checklists/items/delete";
    }

    /**
     * Post handler for deleting a checklist item template.
     * Updates the parent entries to remove the relationships from the entry marked for deletion.
     * Checks that the data is valid and removes the checklist item template from the database.
     *
     * @param id the id of the checklist item template to delete.
     * @param entry the checklist item template database entry.
     * @param result information about the data binding.
     * @return a reference to a checklist item Thymeleaf template.
     */
    @RequestMapping(value = "/checklists/items/delete", method = RequestMethod.POST)
    public String deleteItem(@RequestParam UUID id, @ModelAttribute ChecklistItemTemplate entry, BindingResult result) {
        if (!result.hasErrors()) {
            ChecklistItemTemplate itemTemplate = DBServices.getChecklistItemTemplateService().find(ChecklistItemTemplate.class, id);
            // remove relations first
            Set<ChecklistTemplate> templates = itemTemplate.getTemplates();
            for (ChecklistTemplate template : templates) {
                template.getItems().remove(itemTemplate);
            }
            DBServices.getChecklistTemplateService().update(templates);
            DBServices.getChecklistItemTemplateService().delete(ChecklistItemTemplate.class, itemTemplate);
        }
        return "redirect:/checklists/items";
    }
}
