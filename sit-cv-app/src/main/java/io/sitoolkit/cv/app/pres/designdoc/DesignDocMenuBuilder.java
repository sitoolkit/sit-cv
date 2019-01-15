package io.sitoolkit.cv.app.pres.designdoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;

import io.sitoolkit.cv.app.pres.menu.MenuItem;
import io.sitoolkit.cv.core.infra.util.JsonUtils;

public class DesignDocMenuBuilder {

    private static final String FUNCTION_MODEL_MENU_NAME = "Function Model";

    private Pattern pattern = Pattern.compile("^(.*)\\.(.*?)(\\(.*)$");

    public List<MenuItem> buildItemsAndAppendFunctionModelItems(String menuStr,
            List<String> designDocIds) {

        List<MenuItem> menuItems = JsonUtils.str2obj(menuStr, new TypeReference<List<MenuItem>>() {
        });

        MenuItem functionModelItem = menuItems.stream()
                .filter((item) -> item.getName().equals(FUNCTION_MODEL_MENU_NAME)).findAny().get();
        List<MenuItem> functionModelItems = buildFunctionModelItems(designDocIds);
        functionModelItem.getChildren().addAll(functionModelItems);

        return menuItems;
    }

    private List<MenuItem> buildFunctionModelItems(List<String> designDocIds) {

        List<MenuItem> functionModelItems = new ArrayList<>();

        designDocIds.stream().forEach((id) -> {
            Matcher matcher = pattern.matcher(id);
            matcher.matches();
            String classSignature = matcher.group(1);
            String methodName = matcher.group(2);

            List<MenuItem> classItems = findOrBuildClassItems(functionModelItems, classSignature);

            classItems.add(buildMethodItem(methodName, id));
        });

        return functionModelItems;
    }

    private List<MenuItem> findOrBuildClassItems(List<MenuItem> menuItems, String classSignature) {
        String[] signatureParts = classSignature.split("\\.");

        List<MenuItem> currentItems = menuItems;
        for (String part : signatureParts) {
            Optional<MenuItem> childItem = currentItems.stream()
                    .filter((c) -> c.getName().equals(part)).findAny();

            if (childItem.isPresent()) {
                currentItems = childItem.get().getChildren();
                continue;
            }

            MenuItem newChild = new MenuItem();
            newChild.setName(part);
            currentItems.add(newChild);
            currentItems = newChild.getChildren();
        }

        return currentItems;
    }

    private MenuItem buildMethodItem(String methodName, String designDocId) {
        MenuItem item = new MenuItem();
        item.setName(methodName);
        item.setEndpoint("/designdoc/function/" + designDocId);
        return item;
    }
}
