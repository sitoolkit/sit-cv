package io.sitoolkit.design.pres.designdoc;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DetailResponse {

    private Map<String, String> diagrams = new HashMap<>();
    private Map<String, Map<String, String>> comments = new HashMap<>();

}
