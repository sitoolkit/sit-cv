package org.sitoolkit.design.pres.designdoc;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DetailResponse {

    private Map<String, String> diagrams = new HashMap<>();

}
