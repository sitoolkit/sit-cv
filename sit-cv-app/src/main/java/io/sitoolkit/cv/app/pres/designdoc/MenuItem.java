package io.sitoolkit.cv.app.pres.designdoc;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MenuItem {
    private String name;
    private String endpoint;
    private List<MenuItem> children = new ArrayList<>();
}
