package io.sitoolkit.cv.app.pres.designdoc;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DetailResponse {

    private Map<String, String> diagrams = new HashMap<>();
    private Map<String, String> comments = new HashMap<>();

}
