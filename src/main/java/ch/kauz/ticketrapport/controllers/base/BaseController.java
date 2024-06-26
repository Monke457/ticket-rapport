package ch.kauz.ticketrapport.controllers.base;

import ch.kauz.ticketrapport.security.AuthUser;
import ch.kauz.ticketrapport.services.base.DBServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

/**
 * Base controller containing all universal controller requirements.
 */
public abstract class BaseController {
    @Autowired
    protected AuthUser authUser;
    @Autowired
    protected DBServices services;

    /**
     * Adds the universally required attributes to the model.
     *
     * @param model the model to add the attributes to.
     */
    protected void addBaseAttributes(Model model) {
        model.addAttribute("authUser", authUser.getUser());
        model.addAttribute("returnValue", 1);
    }

    /**
     * Adds the universally required attributes to the model.
     *
     * @param model the model to add the attributes to
     * @param returnValue how far back through the browser history we have to go in order to reach a meaningful page
     */
    protected void addBaseAttributes(Model model, int returnValue) {
        model.addAttribute("authUser", authUser.getUser());
        model.addAttribute("returnValue", returnValue);
    }

    /**
     * Calculates the redirect value depending on the referer page.
     * <p>
     *     This should be used when redirecting after successfully handling a delete request,
     *     so as not to land on a page for an entry that no longer exists.
     * </p>
     * @param returnValue the current return value
     * @param referer the page we originally came from
     * @return an int representing haw far back though the browser history we must go in order to reach a meaningful page.
     */
    protected int getRedirectValue(int returnValue, String referer) {
        return switch (referer) {
            case "details" -> returnValue + 1;
            case "edit" -> returnValue;
            default -> 0;
        };
    }
}
