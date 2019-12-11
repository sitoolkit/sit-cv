package io.sitoolkit.cv.app.pres.functionmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.functionmodel.FunctionModel;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FunctionModelPublisher implements FunctionModelEventListener {

  @Autowired FunctionModelService service;

  @Autowired SimpMessagingTemplate template;

  @MessageMapping("/designdoc/function")
  public void publishDetail(String functionId) {
    DetailResponse response = new DetailResponse();
    FunctionModel functionModel = service.get(functionId);

    functionModel
        .getAllDiagrams()
        .stream()
        .forEach(
            diagram -> {
              String data = new String(diagram.getData());
              response.getDiagrams().put(diagram.getId(), data);
              response.getApiDocs().putAll(diagram.getApiDocs());
            });

    log.info("Publish function model for {}", functionId);

    template.convertAndSend("/topic/designdoc/function/" + functionId, response);
  }

  @Override
  public void onModify(String souceId) {
    publishDetail(souceId);
  }
}
