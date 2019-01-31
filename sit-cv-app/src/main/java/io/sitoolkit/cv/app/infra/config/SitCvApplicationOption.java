package io.sitoolkit.cv.app.infra.config;

public enum SitCvApplicationOption {
    REPORT, PROJECT, ANALYZE_SQL("analyze-sql");

    public static final String PREFIX = "cv";

    private String key;

    private SitCvApplicationOption() {
        this.key = name().toLowerCase();
    }

    private SitCvApplicationOption(String key) {
        this.key = key;
    }

    public String getKey() {
        return PREFIX + "." + key;
    }
}
