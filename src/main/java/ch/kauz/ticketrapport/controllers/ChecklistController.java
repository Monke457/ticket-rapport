package ch.kauz.ticketrapport.controllers;

import ch.kauz.ticketrapport.controllers.base.BaseController;
import ch.kauz.ticketrapport.dtos.TemplateChecklistItemsDTO;
import ch.kauz.ticketrapport.dtos.TemplateItemDTO;
import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.mappers.TemplateMapper;
import ch.kauz.ticketrapport.models.templates.TemplateChecklist;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.models.templates.TemplateItem;
import ch.kauz.ticketrapport.validation.TemplateValidator;
import jakarta.validation.Valid;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * A controller for handling all requests pertaining to template checklist and template item data.
 */
@Controller
public class ChecklistController extends BaseController {

    @Autowired
    private TemplateMapper mapper;
    @Autowired
    private TemplateValidator validator;

    /**
     * Get handler for the template checklists page.
     *
     * @param filter the filter containing search, sort and pagination information
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists")
    public String getIndex(@ModelAttribute("filter") Filter filter, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entries", services.getTemplateChecklistService().find(TemplateChecklist.class, filter).toList());
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", services.getTemplateChecklistService().getPages(TemplateChecklist.class, filter));
        return "checklists/index";
    }

    /**
     * Get handler for the template items page.
     *
     * @param filter the filter containing search, sort and pagination information
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/items")
    public String getItemsIndex(@ModelAttribute("filter") Filter filter, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entries", services.getTemplateItemService().find(TemplateItem.class, filter).toList());
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", services.getTemplateItemService().getPages(TemplateItem.class, filter));
        return "checklists/items/index";
    }

    /**
     * Get handler for the template checklist details page.
     *
     * @param id the id of the template checklist to display
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/details")
    public String getDetails(@RequestParam UUID id, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entry", services.getTemplateChecklistService().get(TemplateChecklist.class, id));
        return "checklists/details";
    }

    /**
     * Get handler for the template item details page.
     *
     * @param id the id of the template item to display
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/items/details")
    public String getItemDetails(@RequestParam UUID id, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entry", services.getTemplateItemService().get(TemplateItem.class, id));
        return "checklists/items/details";
    }

    /**
     * Get handler for the template checklist create page.
     *
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/create")
    public String getCreate(@RequestParam(defaultValue = "1") int returnValue, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("entry", new TemplateChecklist());
        model.addAttribute("selectedItems", new TemplateChecklistItemsDTO());
        model.addAttribute("items", services.getTemplateItemService().getAll(TemplateItem.class).map(TemplateItemDTO::ofEntity));
        return "checklists/create";
    }

    /**
     * Post handler for the template checklist create request.
     * <p>
     *     Creates a new template checklist and persists it in the database.
     * </p>
     *
     * @param entry the {@link TemplateChecklist} object containing the values for the new entry
     * @param result a {@link BindingResult} object containing any error data pertaining to the template checklist
     * @param selectedItems a {@link TemplateChecklistItemsDTO} object containing the values for the associated items of the new entry
     * @param itemsResult a {@link BindingResult} object containing any error data pertaining to the template checklist items
     * @param itemsData a JSON Array containing all information about the selected items
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/checklists/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") TemplateChecklist entry, BindingResult result,
                         @Valid @ModelAttribute("selectedItems") TemplateChecklistItemsDTO selectedItems, BindingResult itemsResult,
                         @RequestParam(value = "itemsData") String itemsData,
                         @RequestParam int returnValue, Model model) {

        try {
            selectedItems.setItems(mapper.mapTemplateItemDTOs(itemsData));
        } catch (JSONException e) {
            return "redirect:/error?code=420";
        }

        validator.validateItems(selectedItems.getItems(), itemsResult);

        if (result.hasErrors() || itemsResult.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("entry", entry);
            model.addAttribute("selectedItems", selectedItems);
            model.addAttribute("items",  mapper.mapAllTemplateItemDTOs(selectedItems));
            return "checklists/create";
        }

        // create checklist template
        services.getTemplateChecklistService().create(entry);

        // create items
        List<TemplateChecklistItems> checklistItems = mapper.mapChecklistItems(entry, selectedItems.getItems());
        services.getTemplateChecklistItemsService().create(checklistItems);

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the template checklist edit page.
     *
     * @param id the id of the {@link TemplateChecklist} to edit
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/edit")
    public String getEdit(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue, @RequestParam(required = false) String referer, Model model) {
        TemplateChecklist entry = services.getTemplateChecklistService().get(TemplateChecklist.class, id);
        TemplateChecklistItemsDTO selectedItems = TemplateChecklistItemsDTO.ofEntity(entry.getItems());

        addBaseAttributes(model, returnValue);

        model.addAttribute("entry", entry);
        model.addAttribute("selectedItems", selectedItems);
        model.addAttribute("items",  mapper.mapAllTemplateItemDTOs(selectedItems));
        model.addAttribute("referer", referer);
        return "checklists/edit";
    }

    /**
     * Post handler for the template checklist edit request.
     * <p>
     *     Updates a template checklist entry in the database.
     * </p>
     *
     * @param entry the {@link TemplateChecklist} object containing updated values
     * @param result a {@link BindingResult} object containing any error data pertaining to the new values
     * @param selectedItems a {@link TemplateChecklistItemsDTO} object containing the updated values for the associated items
     * @param itemsResult a {@link BindingResult} object containing any error data pertaining to the template checklist items
     * @param itemsData a JSON Array containing all information about the selected items
     * @param id the id of the {@link TemplateChecklist} to edit
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/checklists/edit", method = RequestMethod.POST)
    public String edit(@Valid @ModelAttribute("entry") TemplateChecklist entry, BindingResult result,
                       @Valid @ModelAttribute("selectedItems") TemplateChecklistItemsDTO selectedItems, BindingResult itemsResult,
                       @RequestParam(defaultValue = "") String itemsData,
                       @RequestParam UUID id, @RequestParam int returnValue, @RequestParam(required = false) String referer,
                       Model model) {
        try {
            selectedItems.setItems(mapper.mapTemplateItemDTOs(itemsData));
        } catch(JSONException e) {
            return "redirect:/error?code=420";
        }

        validator.validateItems(selectedItems.getItems(), itemsResult);

        if (result.hasErrors() || itemsResult.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("entry", entry);
            model.addAttribute("selectedItems", selectedItems);
            model.addAttribute("items",  mapper.mapAllTemplateItemDTOs(selectedItems));
            model.addAttribute("referer", referer);
            return "checklists/edit";
        }

        // remove original items
        services.getTemplateChecklistItemsService().removeByChecklist(id);

        // save checklist
        services.getTemplateChecklistService().update(entry);

        // create items
        List<TemplateChecklistItems> checklistItems = mapper.mapChecklistItems(entry, selectedItems.getItems());
        services.getTemplateChecklistItemsService().create(checklistItems);

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the template item edit page.
     *
     * @param id the id of the {@link TemplateItem} to edit
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/items/edit")
    public String getEditItem(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue, @RequestParam(required = false) String referer, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("entry", services.getTemplateItemService().get(TemplateItem.class, id));
        model.addAttribute("referer", referer);
        return "checklists/items/edit";
    }

    /**
     * Post handler for the template item edit request.
     * <p>
     *     Updates a template item entry in the database.
     * </p>
     *
     * @param entry a {@link TemplateItem} object containing the updated values
     * @param result  a {@link BindingResult} object containing any error data pertaining to the new values
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/checklists/items/edit", method = RequestMethod.POST)
    public String editItem(@Valid @ModelAttribute("entry") TemplateItem entry, BindingResult result,
                           @RequestParam int returnValue, @RequestParam(required = false) String referer,
                           Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model, returnValue);
            model.addAttribute("entry", entry);
            model.addAttribute("referer", referer);
            return "checklists/items/edit";
        }

        services.getTemplateItemService().update(entry);

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the template checklist delete page.
     *
     * @param id the id of the {@link TemplateChecklist} to delete
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/delete")
    public String getDelete(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue,
                            @RequestParam(defaultValue = "") String referer, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("redirectValue", getRedirectValue(returnValue, referer));
        model.addAttribute("entry", services.getTemplateChecklistService().get(TemplateChecklist.class, id));
        return "checklists/delete";
    }

    /**
     * Post handler for the template checklist delete request.
     * <p>
     *     Removes a template checklist entry permanently from the database.
     * </p>
     *
     * @param id the id of the {@link TemplateChecklist} to delete
     * @param redirectValue how far back through the browser history we need to go in order to reach a meaningful page
     * @param model the model to store the relevant data
     * @return a reference point to a redirect script
     */
    @RequestMapping(value = "/checklists/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @RequestParam(defaultValue = "0") Integer redirectValue, Model model) {
        services.getTemplateChecklistService().delete(TemplateChecklist.class, id);
        model.addAttribute("returnValue", redirectValue + 2);
        return "back";
    }

    /**
     * Get handler for the template item delete page.
     *
     * @param id the id of the {@link TemplateItem} to delete
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/items/delete")
    public String getDeleteItem(@RequestParam UUID id, @RequestParam(defaultValue = "1") int returnValue,
                            @RequestParam(defaultValue = "") String referer, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("redirectValue", getRedirectValue(returnValue, referer));
        model.addAttribute("entry", services.getTemplateItemService().get(TemplateItem.class, id));
        return "checklists/items/delete";
    }

    /**
     * Post handler for the template item delete request.
     * <p>
     *     Removes a template item entry permanently from the database.
     * </p>
     *
     * @param id the id of the {@link TemplateItem} to delete
     * @param redirectValue how far back through the browser history we need to go in order to reach a meaningful page
     * @param model the model to store the relevant data
     * @return a reference point to a redirect script
     */
    @RequestMapping(value = "/checklists/items/delete", method = RequestMethod.POST)
    public String deleteItem(@RequestParam UUID id, @RequestParam(defaultValue = "0") Integer redirectValue, Model model) {
        services.getTemplateItemService().delete(TemplateItem.class, id);
        model.addAttribute("returnValue", redirectValue + 2);
        return "back";
    }

    /**
     * Handler for the get item option request.
     * <p>
     *     Creates a new template item data transfer object and adds it to the model.
     *     <br>
     *     Returns a reference to a fragment that will display the item as a select option.
     * </p>
     *
     * @param description the description of the new template item
     * @param id the id to assign to the new item
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/checklists/getItemOption")
    public String getItemOption(@RequestParam String description, @RequestParam String id, Model model) {
        model.addAttribute("item", TemplateItemDTO.builder().id(id).description(description).selected(true).build());
        return "fragments/form-elements :: multiselect-option";
    }
}
