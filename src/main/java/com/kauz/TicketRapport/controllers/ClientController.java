package com.kauz.TicketRapport.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * A controller for handling all requests pertaining to client data.
 */
@Controller
public class ClientController extends BaseController {
    @GetMapping("/clients")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);
        return "clients";
    }
}
