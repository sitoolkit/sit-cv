package io.sitoolkit.cv.app.pres.function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import io.sitoolkit.cv.core.app.designdoc.DesignDocChangeEventListener;
import io.sitoolkit.cv.core.app.function.FunctionModelService;
import io.sitoolkit.cv.core.domain.function.FunctionModel;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FunctionModelPublisher implements DesignDocChangeEventListener {
    @Autowired
    FunctionModelService service;

    @Autowired
    ProjectManager projectManager;

    @Autowired
    SimpMessagingTemplate template;

    @PostConstruct
    public void init() {
        projectManager.getCurrentProject().getAllSrcDirs().stream().forEach(srcDir -> {
            service.watchDir(srcDir, this);
        });

        service.watchConfig(this);
    }

    @MessageMapping("/designdoc/function")
    public void publishDetail(String functionId) {
        DetailResponse response = new DetailResponse();
        FunctionModel functionModel = service.get(functionId);

        functionModel.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            response.getDiagrams().put(diagram.getId(), data);
            response.getApiDocs().putAll(diagram.getApiDocs());
        });

        log.info("Publish function model for {}", functionId);

        template.convertAndSend("/topic/designdoc/function/" + functionId, response);
    }

    @Override
    public void onDesignDocChange(String designDocId) {
        publishDetail(designDocId);
    }

    @Override
    public void onDesignDocListChange() {
    }
}
