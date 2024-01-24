package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.security.AuthUser;
import com.kauz.TicketRapport.services.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

/**
 * Base controller containing all universal controller requirements.
 * Contains the unit of work and authenticated user
 * as well as a method for adding all universal model attributes.
 */
public abstract class BaseController {
    @Autowired
    protected UnitOfWork unitOfWork;
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
}
