package org.travelmate.controller.viewbean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class LocaleBean implements Serializable {

    private Locale currentLocale;

    @PostConstruct
    public void init() {
        // Get the locale from the browser or default to English
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            currentLocale = facesContext.getExternalContext().getRequestLocale();
            // Make sure we only use supported locales (en or pl)
            if (!currentLocale.getLanguage().equals("pl")) {
                currentLocale = Locale.ENGLISH;
            }
        } else {
            currentLocale = Locale.ENGLISH;
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public String getLanguage() {
        return currentLocale != null ? currentLocale.getLanguage() : "en";
    }

    public void setLanguage(String language) {
        if ("pl".equals(language)) {
            currentLocale = new Locale("pl");
        } else {
            currentLocale = Locale.ENGLISH;
        }
        FacesContext.getCurrentInstance().getViewRoot().setLocale(currentLocale);
    }

    public void switchToEnglish() {
        setLanguage("en");
    }

    public void switchToPolish() {
        setLanguage("pl");
    }

    public boolean isEnglish() {
        return currentLocale != null && "en".equals(currentLocale.getLanguage());
    }

    public boolean isPolish() {
        return currentLocale != null && "pl".equals(currentLocale.getLanguage());
    }
}
