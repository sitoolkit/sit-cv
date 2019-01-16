package io.sitoolkit.cv.app.pres.designdoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.app.pres.menu.MenuItem;

public class DesignDocMenuBuilder {

    private static final Pattern QUALIFIED_METHOD_SIG_PATTERN = Pattern
            .compile("^(.*)\\.(.*?)(\\(.*)$");

    public List<MenuItem> build(List<String> designDocIds) {
        return Arrays.asList(buildDataModelItem(), buildFunctionModelItem(designDocIds));
    }

    private MenuItem buildDataModelItem() {
        MenuItem dataModelItem = MenuItem.builder().name("Data Model").build();

        dataModelItem.getChildren()
                .add(MenuItem.builder().name("CRUD Matrix").endpoint("/designdoc/crud").build());

        return dataModelItem;
    }

    private MenuItem buildFunctionModelItem(List<String> designDocIds) {
        MenuItem functionModelNode = MenuItem.builder().name("Function Model").build();
        functionModelNode.getChildren().addAll(buildFunctionModelItems(designDocIds));
        return functionModelNode;
    }

    private List<MenuItem> buildFunctionModelItems(List<String> designDocIds) {

        List<MenuItem> rootItems = new ArrayList<>();
        // key : path (package name or fqcn)
        Map<String, MenuItem> pathMenuItemMap = new HashMap<>();

        designDocIds.stream().forEach(designDocId -> {
            Matcher matcher = QUALIFIED_METHOD_SIG_PATTERN.matcher(designDocId);
            matcher.matches();
            String fqcn = matcher.group(1);
            String methodName = matcher.group(2);

            MenuItem classItem = findItemWithCreatingParent(fqcn, pathMenuItemMap, rootItems);
            classItem.getChildren().add(buildMethodItem(methodName, designDocId));
        });

        return rootItems;
    }

    private MenuItem findItemWithCreatingParent(String currentPath,
            Map<String, MenuItem> pathMenuItemMap, List<MenuItem> rootItems) {

        MenuItem currentItem = pathMenuItemMap.get(currentPath);

        if (currentItem != null) {
            return currentItem;
        }

        boolean isRoot = !currentPath.contains(".");

        String currentItemName = isRoot ? currentPath
                : StringUtils.substringAfterLast(currentPath, ".");
        currentItem = MenuItem.builder().name(currentItemName).build();
        pathMenuItemMap.put(currentPath, currentItem);

        if (isRoot) {
            rootItems.add(currentItem);
            return currentItem;
        }

        String parentPath = StringUtils.substringBeforeLast(currentPath, ".");
        MenuItem parentItem = findItemWithCreatingParent(parentPath, pathMenuItemMap, rootItems);
        parentItem.getChildren().add(currentItem);

        return currentItem;
    }

    private MenuItem buildMethodItem(String methodName, String designDocId) {
        return MenuItem.builder().name(methodName).endpoint("/designdoc/function/" + designDocId)
                .build();
    }
}
