package io.sitoolkit.cv.app.infra.menu;

import io.sitoolkit.cv.core.infra.util.SitResourceUtils;

public class MenuReader {

    private static final String RESOURCE_PATH = "/menu";

    public String read(String name) {
        return SitResourceUtils.res2str(this, RESOURCE_PATH + "/" + name + ".json");
    }
}
