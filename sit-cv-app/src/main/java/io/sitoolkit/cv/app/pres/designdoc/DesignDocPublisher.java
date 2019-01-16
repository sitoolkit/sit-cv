package io.sitoolkit.cv.app.pres.designdoc;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.sitoolkit.cv.app.pres.menu.MenuItem;
import io.sitoolkit.cv.core.app.function.DesignDocChangeEventListener;
import io.sitoolkit.cv.core.app.function.FunctionModelService;
import io.sitoolkit.cv.core.domain.project.ProjectManager;

@Controller
public class DesignDocPublisher implements DesignDocChangeEventListener {

    @Autowired
    FunctionModelService functionModelService;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    ProjectManager projectManager;

    @Autowired
    DesignDocMenuBuilder menuBuilder;

    List<MenuItem> menuItems;

    @PostConstruct
    public void init() {
        buildMenu();
        publishDesingDocList();

        projectManager.getCurrentProject().getAllSrcDirs().stream().forEach(srcDir -> {
            functionModelService.watchDir(srcDir, this);
        });

        functionModelService.watchConfig(this);
    }

    @MessageMapping("/designdoc/list")
    public void publishDesingDocList() {
        template.convertAndSend("/topic/designdoc/list", menuItems);
    }

    @RequestMapping("")
    public String index() {
        return "index.html";
    }

    @Override
    public void onDesignDocChange(String designDocId) {
    }

    @Override
    public void onDesignDocListChange() {
        buildMenu();
        publishDesingDocList();
    }

    private void buildMenu() {
        menuItems = menuBuilder.build(functionModelService.getAllIds());
    }
}
