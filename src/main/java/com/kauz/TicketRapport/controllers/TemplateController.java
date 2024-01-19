package com.kauz.TicketRapport.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * A controller for handling all requests pertaining to checklist templates.
 */
@Controller
public class TemplateController extends BaseController {
    @GetMapping("/templates")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);
        return "templates";
    }
}
