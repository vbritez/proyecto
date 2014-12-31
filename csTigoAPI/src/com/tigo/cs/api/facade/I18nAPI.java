package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.tigo.cs.commons.jpa.GenericFacadeException;

public abstract class I18nAPI extends AbstractAPI<String> {

    private static ResourceBundle resourceBundle;
    private static Locale currentLocale;
    private static String language;
    private static String country;
    private static final String BASE_NAME = "resources/i18n/LocaleBundle";
    private static boolean configured = false;

    public I18nAPI() {
        super(String.class);
    }

    public void configureInternationalization() {
        try {
            if (!configured) {

                language = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "system.i18n.language");
                country = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "system.i18n.country");
                currentLocale = new Locale(language, country);
                resourceBundle = ResourceBundle.getBundle(BASE_NAME,
                        currentLocale);
                configured = true;
            }
        } catch (GenericFacadeException ex) {
            Logger.getLogger(I18nAPI.class.getName()).log(Level.ERROR, null, ex);
        } catch (Exception e) {
            Logger.getLogger(I18nAPI.class.getName()).log(Level.ERROR, null, e);
        }
    }

    public String iValue(String key) {

        try {
            return getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            getFacadeContainer().getNotifier().info(
                    I18nAPI.class,
                    null,
                    MessageFormat.format(
                            "Sin clave i18n. Clave: {0}. Locale: {1}_{2}", key,
                            getLanguage(), getCountry()));

            return key;
        }
    }

    public String getCountry() {
        return getResourceBundle().getLocale().getCountry();
    }

    public String getLanguage() {
        return getResourceBundle().getLocale().getLanguage();
    }

    public String getLanguageAndCountry() {
        return getLocale().toString();
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            configureInternationalization();
        }
        return resourceBundle;
    }

    public Locale getLocale() {
        return getResourceBundle().getLocale();
    }

}
