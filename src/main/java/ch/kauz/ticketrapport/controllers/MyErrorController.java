package ch.kauz.ticketrapport.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A custom controller for handling error status codes.
 */
@Controller
public class MyErrorController implements ErrorController {

    /**
     * Handles an error by returning the error page with a relevant message.
     * <p>
     *     Can handle http errors 403, 404, 500 and JSONExceptions expressively.
     *     <br>
     *     Any other status code will display a generic message.
     * </p>
     *
     * @param request the request that contains the error
     * @param model a model to store the relevant information
     * @return a reference point for the error Thymeleaf template
     */
    @GetMapping("/error")
    public String handleError(@RequestParam(defaultValue = "0") int code,  HttpServletRequest request, Model model) {
        String message = null;

        if (code == 0) {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

            if (status != null) {
                int statusCode = Integer.parseInt(status.toString());

                if(statusCode == HttpStatus.NOT_FOUND.value()) {
                    message = "Die angeforderte Seite kann nicht gefunden werden.";
                }
                else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    message = "Interne Serverfehler.";
                }
                else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                    message = "Sie sind nicht berechtigt auf diese Seite zuzugreifen.";
                }
            }
        } else if (code == 420) {
            message = "Oopsy daisies in JSON.";
        }

        model.addAttribute("message", message);
        return "error";
    }
}
