package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Role;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.filters.TicketFilter;
import com.kauz.TicketRapport.filters.UserFilter;
import com.kauz.TicketRapport.dtos.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * A controller for handling all requests pertaining to user data.
 */
@Controller
public class UserController extends BaseController {
    @Autowired
    private PasswordEncoder encoder;

    /**
     * A get handler for the users list page. Should only be available to admins.
     * Fetches a filtered and sorted list of all user entries to display.
     *
     * @param model the model containing the relevant view data.
     * @param id the id of a user to display, if present will display details for that user, otherwise displays a list of users.
     * @param search the search string to filter users by.
     * @param roleId the id of the role to filter users by.
     * @param sort a comma separated string of sort orders.
     * @param page the current page in the list.
     * @param asc whether the sort order is ascending.
     * @return a reference to a user Thymeleaf template.
     */
    @GetMapping("/users")
    public String getIndex(Model model,
                           @RequestParam(required = false) UUID id,
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "") UUID roleId,
                           @RequestParam(defaultValue = "lastname") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc) {
        super.addBaseAttributes(model);

        if (id == null) {
            UserFilter filter = new UserFilter(search, sort, page, asc, roleId);
            int pageSize = 10;
            model.addAttribute("entries", DBServices.getUserService().find(User.class, filter, pageSize));
            model.addAttribute("roles", DBServices.getRoleService().getAll(Role.class));
            model.addAttribute("filter", filter);
            model.addAttribute("totalPages", DBServices.getUserService().getPages(User.class, filter, pageSize));
            return "users/index";
        }

        User entry =  DBServices.getUserService().find(User.class, id);
        model.addAttribute("entry", entry);
        
        if (entry.isAdmin()) return "users/details";

        TicketFilter filter = new TicketFilter();
        filter.setLearnerId(id);
        filter.setStatus("In Progress,Completed");

        model.addAttribute("tickets", DBServices.getTicketService().find(Ticket.class, filter)
                .sorted(Comparator.comparing(t -> t.getClient() == null ? "" : t.getClient().getName())));

        filter.setStatus("Closed");
        model.addAttribute("closedCount", DBServices.getTicketService().find(Ticket.class, filter).count());

        return "users/details";
    }

    /**
     * A get handler for the form to create a new user.
     *
     * @param model the model containing the relevant view data.
     * @return a reference to the user create Thymeleaf template.
     */
    @GetMapping("/users/create")
    public String create(Model model, HttpServletRequest request) {
        super.addBaseAttributes(model, request);
        model.addAttribute("entry", new UserDTO());
        model.addAttribute("roles", DBServices.getRoleService().getAll(Role.class));
        return "users/create";
    }

    /**
     * A post handler for creating a new user.
     * Checks that the data is valid and saves a new user entry in the database.
     *
     * @param entry a UserFormData object containing new user data.
     * @param result information about the data binding.
     * @param model the model containing the relevant view data.
     * @return a reference to a Thymeleaf template.
     */
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("entry") UserDTO entry,
                         BindingResult result,
                         Model model,
                         @RequestParam(required = false) String referer) {
        String passError = validatePassword(entry.getPassword(), entry.getConfirmPassword());

        if (result.hasErrors() || passError != null) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("roles", DBServices.getRoleService().getAll(Role.class));
            model.addAttribute("passError", passError);
            model.addAttribute("referer", referer);
            return "users/create";
        }

        User user = new User(entry.getFirstname(), entry.getLastname(), entry.getEmail(), encoder.encode(entry.getPassword()), entry.getRole());

        DBServices.getUserService().create(user);

        if (referer == null) return "redirect:/users";
        return "redirect:" + referer;
    }

    /**
     * Password validation method.
     * Checks that the given passwords are the same.
     *
     * @param pass the password string.
     * @param confirm the confirmed password string, should be the same as pass.
     * @return an error message if the password is invalid, otherwise null.
     */
    private String validatePassword(String pass, String confirm) {
        if (!pass.equals(confirm)) return "The passwords do not match";
        return null;
    }

    /**
     * A get handler for the form to edit a user entry.
     *
     * @param model the model containing the relevant view data.
     * @param id the id of the user to update.
     * @return a reference to the user edit Thymeleaf template.
     */
    @GetMapping("/users/edit")
    public String edit(@RequestParam UUID id,
                       Model model,
                       HttpServletRequest request) {
        super.addBaseAttributes(model, request);
        User user = DBServices.getUserService().find(User.class, id);
        model.addAttribute("entry", user);
        model.addAttribute("roles", DBServices.getRoleService().getAll(Role.class));
        return "users/edit";
    }

    /**
     * A post handler for editing a user entry.
     * Checks that the data is valid and updates the database entry.
     *
     * @param id the id of the user to update.
     * @param entry the new user data.
     * @param result information about the data binding.
     * @param model the model containing the relevant view data.
     * @return a reference to a Thymeleaf template.
     */
    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id,
                       @Valid @ModelAttribute("entry") User entry,
                       BindingResult result,
                       Model model,
                       @RequestParam(required = false) String referer) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("roles", DBServices.getRoleService().getAll(Role.class));
            model.addAttribute("referer", referer);
            return "users/edit";
        }

        User userFromBD = DBServices.getUserService().find(User.class, id);
        entry.setPassword(userFromBD.getPassword());
        DBServices.getUserService().update(entry);

        if(referer == null) return "redirect:/users";
        return "redirect:" + referer;
    }

    /**
     * A get handler for the form to delete a user.
     *
     * @param model the model containing the relevant view data.
     * @param id the id of the user to delete.
     * @return a reference to the user delete Thymeleaf template.
     */
    @GetMapping("/users/delete")
    public String delete(Model model,
                         @RequestParam UUID id,
                         HttpServletRequest request) {
        super.addBaseAttributes(model, request);

        User entry = DBServices.getUserService().find(User.class, id);
        model.addAttribute("entry", entry);

        if (entry.isAdmin()) return "users/delete";

        TicketFilter filter = new TicketFilter();
        filter.setLearnerId(id);
        filter.setStatus("In Progress,Completed");


        model.addAttribute("tickets", DBServices.getTicketService().find(Ticket.class, filter)
                .sorted(Comparator.comparing(t -> t.getClient() == null ? "" : t.getClient().getName())));

        filter.setStatus("Closed");
        model.addAttribute("closedCount", DBServices.getTicketService().find(Ticket.class, filter).count());

        return "users/delete";
    }

    /**
     * A post handler for deleting a user.
     * First removes the user from and tickets.
     *
     * @param id the id of the user to delete.
     * @param entry the user entry to delete.
     * @param result information about the data binding.
     * @return a reference to the user list Thymeleaf template.
     */
    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id,
                         @ModelAttribute User entry,
                         BindingResult result,
                         @RequestParam(required = false) String referer) {
        if (!result.hasErrors()) {
            // remove user from tickets first
            TicketFilter filter = new TicketFilter();
            filter.setLearnerId(id);
            List<Ticket> userTickets = DBServices.getTicketService().find(Ticket.class, filter).toList();
            for (Ticket ticket : userTickets) {
                ticket.setAssignedUser(null);
            }
            DBServices.getTicketService().update(userTickets);
            DBServices.getUserService().delete(User.class, entry);
        }

        if (referer == null) return "redirect:/users";
        return "redirect:" + referer;
    }
}
