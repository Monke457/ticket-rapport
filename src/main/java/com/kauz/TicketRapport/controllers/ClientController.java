package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.filters.Filter;
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
    @GetMapping("/clients")
    public String getIndex(Model model,
                           @RequestParam(required = false) UUID id,
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "name") String sort,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "true") boolean asc) {
        super.addBaseAttributes(model);

        if (id == null) {
            Filter filter = new Filter(search, sort, page, asc);
            model.addAttribute("entries", unitOfWork.getClientService().find(Client.class, filter));
            model.addAttribute("filter", filter);
            return "clients/index";
        }

        model.addAttribute("entry", unitOfWork.getClientService().find(Client.class, id));
        return "clients/details";
    }

    @GetMapping("/clients/create")
    public String create(Model model) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", new Client());
        return "clients/create";
    }

    @RequestMapping(value = "/clients/create", method = RequestMethod.POST)
    public String create(@ModelAttribute Client entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            super.addBaseAttributes(model);
            model.addAttribute("entry", entry);
            return "clients/create";
        }
        unitOfWork.getClientService().create(entry);
        return "redirect:/clients";
    }

    @GetMapping("/clients/edit")
    public String edit(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        Client client = unitOfWork.getClientService().find(Client.class, id);
        model.addAttribute("entry", client);
        return "clients/edit";
    }

    @RequestMapping(value = "/clients/edit", method = RequestMethod.POST)
    public String edit(@RequestParam UUID id, @ModelAttribute Client entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addBaseAttributes(model);
            model.addAttribute("entry", entry);
            return "clients/edit";
        }
        unitOfWork.getClientService().update(entry);
        return "redirect:/clients";
    }

    @GetMapping("/clients/delete")
    public String delete(Model model, @RequestParam UUID id) {
        super.addBaseAttributes(model);
        model.addAttribute("entry", unitOfWork.getClientService().find(Client.class, id));
        return "clients/delete";
    }

    @RequestMapping(value = "/clients/delete", method = RequestMethod.POST)
    public String delete(@RequestParam UUID id, @ModelAttribute Client entry, BindingResult result) {
        if (!result.hasErrors()) {
            unitOfWork.getClientService().delete(Client.class, entry);
        }
        return "redirect:/clients";
    }
}
