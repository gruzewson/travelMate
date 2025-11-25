package org.travelmate.controller.viewbean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.travelmate.model.User;
import org.travelmate.service.UserService;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;

@Named
@SessionScoped
public class AuthBean implements Serializable {

    private transient SecurityContext securityContext;
    private transient UserService userService;
    private User currentUser;

    private SecurityContext getSecurityContext() {
        if (securityContext == null) {
            securityContext = CDI.current().select(SecurityContext.class).get();
        }
        return securityContext;
    }

    private UserService getUserService() {
        if (userService == null) {
            userService = CDI.current().select(UserService.class).get();
        }
        return userService;
    }

    public String getUsername() {
        Principal principal = getSecurityContext().getCallerPrincipal();
        return principal != null ? principal.getName() : null;
    }

    public boolean isLoggedIn() {
        return getSecurityContext().getCallerPrincipal() != null;
    }

    public boolean isAdmin() {
        return getSecurityContext().isCallerInRole("ADMIN");
    }

    public boolean isUser() {
        return getSecurityContext().isCallerInRole("USER");
    }

    public User getCurrentUser() {
        if (currentUser == null && isLoggedIn()) {
            String username = getUsername();
            if (username != null) {
                currentUser = getUserService().findByLogin(username).orElse(null);
            }
        }
        return currentUser;
    }

    public void logout() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

            // Clear cached user
            currentUser = null;

            // Logout from security context
            request.logout();

            // Invalidate session
            externalContext.invalidateSession();

            // Redirect to login page
            externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

