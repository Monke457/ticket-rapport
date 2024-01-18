package com.kauz.TicketRapport.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * A controller for handling all requests pertaining to ticket data.
 */
@Controller
public class TicketController extends BaseController {
    @GetMapping("/tickets")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);
        return "tickets";
    }
}
