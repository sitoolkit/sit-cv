package io.sitoolkit.cv.app.pres.designdoc;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ListResponse {
    private List<String> designDocIds = new ArrayList<>();
}
