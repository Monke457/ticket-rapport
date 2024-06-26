package ch.kauz.ticketrapport.controllers;

import ch.kauz.ticketrapport.controllers.base.BaseController;
import ch.kauz.ticketrapport.dtos.TicketDTO;
import ch.kauz.ticketrapport.dtos.UserDTO;
import ch.kauz.ticketrapport.filters.UserFilter;
import ch.kauz.ticketrapport.models.Role;
import ch.kauz.ticketrapport.models.User;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

/**
 * A controller for handling all requests pertaining to user data.
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Get handler for the users page.
     *
     * @param filter the filter containing search, sort and pagination information
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/users")
    public String getIndex(@ModelAttribute("filter") UserFilter filter, Model model) {
        addBaseAttributes(model);
        model.addAttribute("entries", services.getUserService().find(User.class, filter).toList());
        model.addAttribute("filter", filter);
        model.addAttribute("totalPages", services.getUserService().getPages(User.class, filter));
        model.addAttribute("roles", services.getRoleService().getAll(Role.class));
        return "users/index";
    }

    /**
     * Get handler for the user details page.
     *
     * @param id the id of the ticket to display
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/users/details")
    public String getDetails(@RequestParam UUID id, Model model) {
        User entry = services.getUserService().get(User.class, id);

        addBaseAttributes(model);
        model.addAttribute("entry", entry);
        model.addAttribute("openTickets", entry.getTickets().stream()
                .filter(t -> !t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .map(TicketDTO::ofEntity).toList());
        model.addAttribute("closedTickets", entry.getTickets().stream()
                .filter(t -> t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .count());
        return "users/details";
    }

    /**
     * Get handler for the user create page.
     *
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/users/create")
    public String getCreate(@RequestParam(defaultValue = "1") int returnValue, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("entry", new UserDTO());
        model.addAttribute("roles", services.getRoleService().getAll(Role.class));
        return "users/create";
    }

    /**
     * Post handler for the user create request.
     * <p>
     *     Creates a new user and persists it in the database.
     * </p>
     *
     * @param entry a {@link UserDTO} object containing the values for the new entry
     * @param result a {@link BindingResult} object containing any validation errors pertaining to the new values
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") UserDTO entry, BindingResult result,
                         @RequestParam int returnValue, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model, returnValue);
            convertBindingResults(result);
            model.addAttribute("entry", entry);
            model.addAttribute("roles", services.getRoleService().getAll(Role.class));
            return "users/create";
        }

        User user = new User(entry.getFirstname(), entry.getLastname(), entry.getEmail(), encoder.encode(entry.getPassword()),
                services.getRoleService().get(Role.class, entry.getRoleId()), Set.of());

        services.getUserService().create(user);

        model.addAttribute("returnValue", returnValue);
        return "back";
    }

    /**
     * Get handler for the user edit page.
     *
     * @param id the id of the {@link User} to edit
     * @param returnValue the current return value
     * @param referer the page we came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/users/edit")
    public String getEdit(@RequestParam UUID id, @RequestParam(required = false) String referer,
                          @RequestParam(defaultValue = "1") int returnValue, Model model) {
        addBaseAttributes(model, returnValue);
        model.addAttribute("referer", referer);
        model.addAttribute("entry", services.getUserService().get(User.class, id));
        model.addAttribute("roles", services.getRoleService().getAll(Role.class));
        return "users/edit";
    }

    /**
     * Post handler for the user edit request.
     * <p>
     *     Updates a user entry in the database.
     * </p>
     *
     * @param id the id of the {@link User} to update
     * @param entry a {@link User} object containing the values for the new entry
     * @param result a {@link BindingResult} object containing any validation errors pertaining to the new values
     * @param returnValue the current return value
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id, @Valid @ModelAttribute("entry") User entry, BindingResult result,
                       @RequestParam(required = false) String referer,
                       @RequestParam int returnValue, Model model) {
        if (!result.hasErrors()) {
            try {
                entry.setPassword(services.getUserService().getPassword(id));
                services.getUserService().update(entry);
                model.addAttribute("returnValue", returnValue);
                return "back";

            } catch (DataIntegrityViolationException e) {
                result.rejectValue("email", "UniqueEmail", "Ein Konto existiert bereits unter diesem Email");
            }
        }

        addBaseAttributes(model, returnValue);
        model.addAttribute("referer", referer);
        model.addAttribute("entry", entry);
        model.addAttribute("roles", services.getRoleService().getAll(Role.class));
        return "users/edit";
    }

    /**
     * Get handler for the user delete page.
     *
     * @param id the id of the {@link User} to delete
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @param model the model to store the relevant data
     * @return a reference point for the Thymeleaf template
     */
    @GetMapping("/users/delete")
    public String getDelete(@RequestParam UUID id, @RequestParam(defaultValue = "1") Integer returnValue,
                            @RequestParam(defaultValue = "") String referer, Model model) {
        User entry = services.getUserService().get(User.class, id);

        addBaseAttributes(model, returnValue);
        model.addAttribute("redirectValue", getRedirectValue(returnValue, referer));
        model.addAttribute("entry", entry);
        model.addAttribute("openTickets", entry.getTickets().stream()
                .filter(t -> !t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .map(TicketDTO::ofEntity).toList());
        model.addAttribute("closedTickets", entry.getTickets().stream()
                .filter(t -> t.getStatus().getDescription().equals(StatusType.CLOSED.getDisplay()))
                .count());
        return "users/delete";
    }

    /**
     * Post handler for the user delete request.
     * <p>
     *     Removes a user entry permanently from the database.
     * </p>
     *
     * @param id the id of the {@link User} to delete
     * @param redirectValue how far back through the browser history we need to go in order to reach a meaningful page
     * @param model the model to store the relevant data
     * @return a reference point to a redirect script
     */
    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @RequestParam(defaultValue = "0") Integer redirectValue, Model model) {
        services.getUserService().delete(User.class, id);
        model.addAttribute("returnValue", redirectValue + 2);
        return "back";
    }

    /**
     * Rejects the passwordConfirm value if there is a field match error.
     *
     * @param result the {@link BindingResult} object to store the errors in
     */
    private void convertBindingResults(BindingResult result) {
        for (ObjectError err : result.getGlobalErrors()) {
            if (err.getDefaultMessage() == null) {
                continue;
            }
            if ("FieldsMatch".equals(err.getCode())) {
                result.rejectValue("passwordConfirm", err.getCode(), err.getDefaultMessage());
            }
        }
    }
}
