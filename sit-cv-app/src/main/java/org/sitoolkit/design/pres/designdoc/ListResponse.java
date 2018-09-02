package org.sitoolkit.design.pres.designdoc;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ListResponse {
    private List<String> designDocIds = new ArrayList<>();
}
