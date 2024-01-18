package com.kauz.TicketRapport.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * A controller for handling all requests on the homepage.
 */
@Controller
public class HomeController extends BaseController {
    @GetMapping("/")
    public String getIndex(Model model) {
        super.addBaseAttributes(model);
        return "index";
    }
}
