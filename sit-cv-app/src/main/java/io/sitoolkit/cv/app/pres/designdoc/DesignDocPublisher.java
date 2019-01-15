package io.sitoolkit.cv.app.pres.designdoc;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DesignDocPublisher {

    @Autowired
    DesignDocService service;

    @Autowired
    SimpMessagingTemplate template;

    @RequestMapping("")
    public String index() {
        return "index.html";
    }

    @MessageMapping("/designdoc/list")
    public void publishDesingDocList() {

        ListResponse listResponse = new ListResponse();
        listResponse.getDesignDocIds().addAll(service.getAllIds());

        // TODO 誰がjsonを読むべきか
        // TODO 誰がMenuItemを作るべきか
        // TODO name: Function Model を綺麗に指定して取得する方法はあるか
        String menuItemStr = SitResourceUtils.res2str(this, "/menu.json");
        List<MenuItem> menuItems = JsonUtils.str2obj(menuItemStr, new TypeReference<List<MenuItem>>() {});
        MenuItem functionModelItem = menuItems.stream().filter((item) -> item.getName().equals("Function Model")).findAny().get();

        Pattern pattern = Pattern.compile("^(.*)\\.(.*?)(\\(.*)$");
        service.getAllIds().stream().forEach((id) -> {
            Matcher matcher = pattern.matcher(id);
            if (!matcher.matches()) {
                return;
            }
            String pkg = matcher.group(1);
            String methodName = matcher.group(2);

            String[] packageNames = pkg.split("\\.");
            MenuItem endItem = Stream.of(packageNames).reduce(functionModelItem, (item, name) -> {
                Optional<MenuItem> childItem = item.getChildren().stream().filter((c) -> c.getName().equals(name)).findAny();
                return childItem.orElseGet(() -> {
                    MenuItem child = new MenuItem();
                    child.setName(name);
                    item.getChildren().add(child);
                    return child;
                });
            }, (a, b) -> {
                // TODO cannnot merge?
                return a;
            });

            MenuItem newItem = new MenuItem();
            newItem.setName(methodName);
            newItem.setEndpoint("/designdoc/function/" + id);
            endItem.getChildren().add(newItem);
        });

        template.convertAndSend("/topic/designdoc/list", menuItems);
    }
}
