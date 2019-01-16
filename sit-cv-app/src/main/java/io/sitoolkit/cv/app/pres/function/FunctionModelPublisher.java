package io.sitoolkit.cv.app.pres.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import io.sitoolkit.cv.app.pres.designdoc.DetailResponse;
import io.sitoolkit.cv.core.app.function.FunctionModelService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FunctionModelPublisher {
    @Autowired
    FunctionModelService service;

    @Autowired
    SimpMessagingTemplate template;

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
}
