package io.sitoolkit.cv.app.pres.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import io.sitoolkit.cv.app.pres.designdoc.DetailResponse;
import io.sitoolkit.cv.core.app.function.FunctionModelService;
import io.sitoolkit.cv.core.domain.function.FunctionModel;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FunctionModelPublisher {
    @Autowired
    FunctionModelService service;

    @Autowired
    SimpMessagingTemplate template;

    @MessageMapping("/designdoc/detail")
    public void publishDetail(String functionId) {
        DetailResponse response = new DetailResponse();
        FunctionModel functionModel = service.get(functionId);

        functionModel.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            response.getDiagrams().put(diagram.getId(), data);
            response.getApiDocs().putAll(diagram.getApiDocs());
        });

        log.info("Publish function model for {}", functionId);

        template.convertAndSend("/topic/designdoc/detail/" + functionId, response);
    }
}
