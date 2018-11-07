package io.sitoolkit.cv.core.infra.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageManager {

    private static ResourceBundle resource;

    private static synchronized ResourceBundle getResource() {
        if (resource == null) {
            String baseName = MessageManager.class.getPackage().getName().replace(".", "/")
                    + "/message";
            log.info("message resource is initialised with locale {}", Locale.getDefault());
            resource = ResourceBundle.getBundle(baseName);
        }
        return resource;
    }

    public static String getMessage(String key) {
        try {
            return getResource().getString(key);
        } catch (MissingResourceException e) {
            log.warn("{}, locale {}", e.getMessage(), getResource().getLocale());
        }
        return "!! messing resource !!";

    }

}
