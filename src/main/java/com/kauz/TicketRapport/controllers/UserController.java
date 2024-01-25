package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Role;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.models.filters.UserFilter;
import com.kauz.TicketRapport.models.helpers.UserFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * A controller for handling all requests pertaining to user data.
 */
@Controller
public class UserController extends BaseController {
    @Autowired
    private PasswordEncoder encoder;

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
            model.addAttribute("entries", unitOfWork.getUserService().find(User.class, filter, pageSize));
            model.addAttribute("roles", unitOfWork.getRoleService().getAll(Role.class));
            model.addAttribute("filter", filter);
            model.addAttribute("totalPages", unitOfWork.getUserService().getPages(User.class, filter, pageSize));
            return "users/index";
        }

        model.addAttribute("entry", unitOfWork.getUserService().find(User.class, id));
        return "users/details";
    }

    @GetMapping("/users/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", new UserFormData());
        model.addAttribute("roles", unitOfWork.getRoleService().getAll(Role.class));
        return "users/create";
    }

    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public String create(@ModelAttribute UserFormData entry,
                         BindingResult result, Model model) {
        boolean passError = !validatePassword(entry.getPassword(),
                entry.getConfirmPassword());

        if (result.hasErrors() || passError) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("roles",
                    unitOfWork.getRoleService().getAll(Role.class));
            return "users/create";
        }

        unitOfWork.getUserService().create(new User(entry.getFirstname(),
                entry.getLastname(), entry.getEmail(),
                encoder.encode(entry.getPassword()), entry.getRole()));
        return "redirect:/users";
    }

    private boolean validatePassword(String pass, String confirm) {
        // @TODO: add validation error messages
        if (pass.isBlank()) return false;
        if (confirm.isBlank()) return false;
        if (!pass.equals(confirm)) return false;
        return pass.length() >= 5;
    }

    @GetMapping("/users/edit")
    public String edit(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        User user = unitOfWork.getUserService().find(User.class, id);
        model.addAttribute("entry", user);
        model.addAttribute("roles", unitOfWork.getRoleService().getAll(Role.class));
        return "users/edit";
    }

    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id, @ModelAttribute User entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            model.addAttribute("roles", unitOfWork.getRoleService().getAll(Role.class));
            return "users/edit";
        }
        unitOfWork.getUserService().update(entry);
        return "redirect:/users";
    }

    @GetMapping("/users/delete")
    public String delete(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getUserService().find(User.class, id));
        return "users/delete";
    }

    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute User entry, BindingResult result) {
        if (!result.hasErrors()) {
            unitOfWork.getUserService().delete(User.class, entry);
        }
        return "redirect:/users";
    }
}
