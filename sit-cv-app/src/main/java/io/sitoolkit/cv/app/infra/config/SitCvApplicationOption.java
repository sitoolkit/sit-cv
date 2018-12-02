package io.sitoolkit.cv.app.infra.config;

public enum SitCvApplicationOption {
    REPORT, PROJECT;

    public static final String PREFIX = "cv";

    public String getKey() {
        return PREFIX + "." + name().toLowerCase();
    }
}
