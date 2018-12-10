package io.sitoolkit.cv.core.infra.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyUtils {
    private static final String PROXYIES_PROPERTY = "java.net.useSystemProxies";

    private ProxyUtils() {
    }

    public static void setToUseSystemSettings() {
        if (System.getProperty(PROXYIES_PROPERTY) == null) {
            System.setProperty(PROXYIES_PROPERTY, "true");
        }

        if (System.getProperty(PROXYIES_PROPERTY).equals("true")) {
            log.info("Use system proxy settings");
        }
    }
}
