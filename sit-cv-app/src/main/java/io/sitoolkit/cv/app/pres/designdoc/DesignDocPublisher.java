package io.sitoolkit.cv.app.pres.designdoc;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.sitoolkit.cv.core.app.designdoc.DesignDocChangeEventListener;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
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

    @PostConstruct
    public void init() {
        publishDesingDocList();

        projectManager.getCurrentProject().getAllSrcDirs().stream().forEach(srcDir -> {
            functionModelService.watchDir(srcDir, this);
        });

        functionModelService.watchConfig(this);
    }

    @MessageMapping("/designdoc/list")
    public void publishDesingDocList() {
        template.convertAndSend("/topic/designdoc/list",
                menuBuilder.build(functionModelService.getAllIds()));
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
        publishDesingDocList();
    }

}
