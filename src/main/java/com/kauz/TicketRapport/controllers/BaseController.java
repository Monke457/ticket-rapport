package com.kauz.TicketRapport.controllers;

import com.kauz.TicketRapport.security.AuthUser;
import com.kauz.TicketRapport.services.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public abstract class BaseController {
    @Autowired
    protected UnitOfWork unitOfWork;
    @Autowired
    protected AuthUser authUser;

    protected void addBaseAttributes(Model model) {
        model.addAttribute("authUser", authUser.getUser());
    }
}
