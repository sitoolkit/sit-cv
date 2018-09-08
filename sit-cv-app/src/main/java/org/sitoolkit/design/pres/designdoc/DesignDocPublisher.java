package org.sitoolkit.design.pres.designdoc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.annotation.PostConstruct;

import org.sitoolkit.cv.core.app.designdoc.DesignDocService;
import org.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import org.sitoolkit.design.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DesignDocPublisher {

    @Autowired
    DesignDocService service;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    ApplicationConfig config;

    @PostConstruct
    public void init() {
        log.debug("loading project:{}", config.getTargetProjectPath());

        Path srcDir = Paths.get(config.getTargetProjectPath(), "src/main/java");
        service.loadDir(srcDir);

        ListResponse listResponse = buildDesingDocList();
        template.convertAndSend("/topic/designdoc/list", listResponse);

        service.watchDir(srcDir, entryPoint -> {
            publishDetail(entryPoint);
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
            String data = "data:image/png;base64,"
                    + Base64.getEncoder().encodeToString(diagram.getData());
            response.getDiagrams().put(diagram.getId(), data);
        });
        template.convertAndSend("/topic/designdoc/detail/" + designDocId, response);
    }

}
