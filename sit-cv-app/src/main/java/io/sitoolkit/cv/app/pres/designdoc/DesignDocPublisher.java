package io.sitoolkit.cv.app.pres.designdoc;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.sitoolkit.cv.app.infra.config.ApplicationConfig;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.project.ProjectManager;

@Controller
public class DesignDocPublisher {

    @Autowired
    DesignDocService service;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    ApplicationConfig config;

    @Autowired
    ProjectManager projectManager;

    @PostConstruct
    public void init() {

        ListResponse listResponse = buildDesingDocList();
        template.convertAndSend("/topic/designdoc/list", listResponse);

        projectManager.getCurrentProject().getWatchDirs().stream().forEach(srcDir -> {
            service.watchDir(srcDir, entryPoint -> {
                publishDetail(entryPoint);
            });
        });
    }

    @MessageMapping("/designdoc/list")
    @SendTo("/topic/designdoc/list")
    public ListResponse buildDesingDocList() {

        ListResponse listResponse = new ListResponse();
        listResponse.getDesignDocIds().addAll(service.getAllIds());

        return listResponse;
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
        template.convertAndSend("/topic/designdoc/detail/" + designDocId, response);
    }

    @RequestMapping("")
    public String index() {
        return "index.html";
    }
}
