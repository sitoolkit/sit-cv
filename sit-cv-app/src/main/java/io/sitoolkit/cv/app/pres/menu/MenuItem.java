package io.sitoolkit.cv.app.pres.menu;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class MenuItem {
    private String name;
    private String endpoint;
    @Default
    private List<MenuItem> children = new ArrayList<>();
}
