package io.sitoolkit.cv.app.pres.designdoc;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;

@Controller
public class DesignDocPublisher implements DesignDocTreeEventListener {

  @Autowired
  DesignDocService designDocService;

  @Autowired
  SimpMessagingTemplate template;

  @PostConstruct
  public void init() {
    publishDesingDocList();
  }

  @MessageMapping("/designdoc/list")
  public void publishDesingDocList() {
    template.convertAndSend("/topic/designdoc/list", designDocService.buildMenu());
  }

  @RequestMapping("")
  public String index() {
    return "index.html";
  }

  @Override
  public void onModified() {
    publishDesingDocList();
  }

}
