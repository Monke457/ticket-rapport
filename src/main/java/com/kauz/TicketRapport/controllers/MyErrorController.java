package com.kauz.TicketRapport.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * A custom error handling controller.
 * Returns access denied message to 403 errors and redirects to the login if a database user cannot be found using session data.
 * All other errors show a styled generic error message.
 */
@Controller
public class MyErrorController extends BaseController implements ErrorController {

    @GetMapping("/error")
    public String error(Model model, HttpServletRequest request) {
        if (authUser.getUser() == null) return "redirect:/login";

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status == null || !Objects.equals(status.toString(), "403")) {
            model.addAttribute("messageProperty", "error.general");
        } else {
            model.addAttribute("messageProperty", "error.permission");
        }

        super.addBaseAttributes(model);
        return "error";
    }
}