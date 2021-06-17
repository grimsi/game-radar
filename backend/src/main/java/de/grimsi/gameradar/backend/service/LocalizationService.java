package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class LocalizationService {

    @Autowired
    private Logger log;

    @Autowired
    private ApplicationProperties config;

    /**
     * Returns a resource bundle with a given localization.
     * Returns the default localization if no bundle for the given localization exists.
     *
     * @param locale the localization
     * @return localized resource bundle
     */
    public ResourceBundle getLocalizedMessages(Locale locale) {
        final String bundleName = "i18n";

        log.debug("get localized messages for locale {}", locale.getCountry());

        ResourceBundle messages = ResourceBundle.getBundle(bundleName, locale);

        // if there is no bundle with the given locale found, return the one with the default locale
        if (messages.getLocale() != locale) {
            return ResourceBundle.getBundle(bundleName, config.getDefaultLocale());
        }

        return messages;
    }

    /**
     * Returns a string from a key in a given localization (or the default one if no translation could be found)
     *
     * @param messageKey the key under which the string can be found
     * @param locale     the locale in which the string should be searched
     * @return the localized string
     */
    public String getLocalizedMessage(String messageKey, Locale locale) {
        log.debug("get localized message with key '{}' for locale {}", messageKey, locale.getCountry());
        return getLocalizedMessages(locale).getString(messageKey);
    }

    /**
     * Returns a string from a key in the default localization
     *
     * @param messageKey the key under which the string can be found
     * @return the localized string
     */
    public String getLocalizedMessage(String messageKey) {
        log.debug("get localized message with key '{}' for default locale {}", messageKey, config.getDefaultLocale().getCountry());
        return getLocalizedMessages(config.getDefaultLocale()).getString(messageKey);
    }
}
