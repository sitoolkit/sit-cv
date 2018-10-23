package io.sitoolkit.cv.app.pres.designdoc;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DetailResponse {

    private Map<String, String> diagrams = new LinkedHashMap<>();
    private Map<String, String> comments = new LinkedHashMap<>();

}
