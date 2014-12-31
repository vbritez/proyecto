package ws.tigomoney.tigo.com;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;



@ManagedBean(name = "iBean")
@ApplicationScoped
public class I18nBean {

    private static ResourceBundle resourceBundle;
    // private static ResourceBundle reportResourceBundle;
    private static Locale currentLocale;
    private static String language;
    private static String country;
    private static Locale locale;
    private static String DEFAULT_LANGUAGE = "es";
    private static String DEFAULT_COUNTRY = "PY";
    private static final String BASE_NAME = "resources/i18n/LocaleBundle";

    // private static final String REPORT_BASE_NAME =
    // "resources/i18n/ReportLocaleBundle";

    @PostConstruct
    public static void configureInternationalization() {
        try {

            language = "es";
            country = "PY";
            currentLocale = new Locale(language, country);
            locale = new Locale(language, country);
            resourceBundle = ResourceBundle.getBundle(BASE_NAME, currentLocale);
            System.out.println(iValue("web.client.commons.ConfiguringI18n").concat("......"));
        } catch (Exception e) {
            Logger.getLogger(I18nBean.class).log(Level.ERROR, null, e);
        }
    }

    public static ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null) {
                resourceBundle = ResourceBundle.getBundle(BASE_NAME, currentLocale);
            }
        } catch (Exception e) {
            Logger.getLogger(I18nBean.class.getName()).log(Level.ERROR, null, e);
        }
        return resourceBundle;
    }

    public static void defaultInternationalization() {
        currentLocale = new Locale("es", "PY");
        resourceBundle = ResourceBundle.getBundle(BASE_NAME, currentLocale);
    }

    public static String preValidateIvalue(String key) {
        if (resourceBundle == null) {
            defaultInternationalization();
        }
        if (resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        } else {
            return key;
        }
    }

    public static String iValue(String key) {
    	if (resourceBundle == null){
    		configureInternationalization();
    	}
        return resourceBundle.getString(key);
    }

    public void defaultLocale() {
        language = DEFAULT_LANGUAGE;
        country = DEFAULT_COUNTRY;
        currentLocale = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
    }

    public String getCountry() {
        if (resourceBundle == null) {
            defaultLocale();
        }
        return country;
    }

    public String getLanguage() {
        if (resourceBundle == null) {
            defaultLocale();
        }
        return language;
    }

    public String getLanguageAndCountry() {
        if (resourceBundle == null) {
            defaultLocale();
        }
        return language + "-" + country.toLowerCase();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

   public String getLocale() {
        if (resourceBundle == null) {
            defaultLocale();
        }
        return language.toLowerCase() + "_" + country.toUpperCase();
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

}
