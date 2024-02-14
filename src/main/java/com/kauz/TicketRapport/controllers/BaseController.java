package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.security.AuthUser;
import com.kauz.TicketRapport.services.DBServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Base controller containing all universal controller requirements.
 * Contains the unit of work and authenticated user
 * as well as a method for adding all universal model attributes.
 */
public abstract class BaseController {
    @Autowired
    protected DBServices DBServices;
    @Autowired
    protected AuthUser authUser;

    /**
     * Adds the universally required attribute of the authenticated user to the model.
     * All controller handlers that lead to an endpoint in the app must call this method.
     * 
     * @param model the model to add the attributes to.
     */
    protected void addBaseAttributes(Model model) {
        model.addAttribute("authUser", authUser.getUser());
    }

    protected void addBaseAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("authUser", authUser.getUser());
        model.addAttribute("referer", request.getHeader("referer"));
    }
}
