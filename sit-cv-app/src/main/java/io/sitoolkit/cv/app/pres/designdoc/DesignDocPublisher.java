package io.sitoolkit.cv.app.pres.designdoc;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.sitoolkit.cv.app.infra.config.ApplicationConfig;
import io.sitoolkit.cv.app.infra.menu.MenuReader;
import io.sitoolkit.cv.app.pres.menu.MenuItem;
import io.sitoolkit.cv.core.app.designdoc.DesignDocChangeEventListener;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DesignDocPublisher implements DesignDocChangeEventListener {

    @Autowired
    DesignDocService service;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    ApplicationConfig config;

    @Autowired
    ProjectManager projectManager;

    @Autowired
    MenuReader menuReader;

    @Autowired
    DesignDocMenuBuilder menuBuilder;

    @PostConstruct
    public void init() {

        publishDesingDocList();

        projectManager.getCurrentProject().getAllSrcDirs().stream().forEach(srcDir -> {
            service.watchDir(srcDir, this);
        });

        service.watchConfig(this);
    }

    @MessageMapping("/designdoc/list")
    public void publishDesingDocList() {
        String menuStr = menuReader.read("designdoc");

        List<MenuItem> menuItems = menuBuilder
                .buildItemsAndAppendFunctionModelItems(menuStr, service.getAllIds());

        template.convertAndSend("/topic/designdoc/list", menuItems);
    }

    @MessageMapping("/designdoc/detail")
    public void publishDetail(String designDocId) {
        DetailResponse response = new DetailResponse();
        DesignDoc designDoc = service.get(designDocId);

        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            response.getDiagrams().put(diagram.getId(), data);
            response.getApiDocs().putAll(diagram.getApiDocs());
        });

        log.info("Publish design doc for {}", designDocId);

        template.convertAndSend("/topic/designdoc/detail/" + designDocId, response);
    }

    @RequestMapping("")
    public String index() {
        return "index.html";
    }

    @Override
    public void onDesignDocChange(String designDocId) {
        publishDetail(designDocId);
    }

    @Override
    public void onDesignDocListChange() {
        publishDesingDocList();
    }
}
