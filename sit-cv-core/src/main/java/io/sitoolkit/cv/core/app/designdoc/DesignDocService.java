package io.sitoolkit.cv.core.app.designdoc;

import java.util.List;

import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDocMenuBuilder;
import io.sitoolkit.cv.core.domain.menu.MenuItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DesignDocService {

    @NonNull
    FunctionModelService functionModelService;

    @NonNull
    DesignDocMenuBuilder menuBuilder;

    public List<MenuItem> buildMenu() {
        return menuBuilder.build(functionModelService.getAllIds());
    }
}
