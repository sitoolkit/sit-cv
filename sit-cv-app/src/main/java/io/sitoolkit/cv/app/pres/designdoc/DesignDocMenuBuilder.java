package io.sitoolkit.cv.app.pres.designdoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.sitoolkit.cv.app.pres.menu.MenuItem;

public class DesignDocMenuBuilder {

    private Pattern designDocIdpattern = Pattern.compile("^(.*)\\.(.*?)(\\(.*)$");

    public List<MenuItem> buildItemsAndAppendFunctionModelItems(List<String> designDocIds) {

        List<MenuItem> menuItems = buildMenuItems();
        menuItems.add(buildFunctionModelItem(designDocIds));

        return menuItems;
    }

    private List<MenuItem> buildMenuItems() {
        MenuItem crudMatrix = MenuItem.builder().name("CRUD Matrix").endpoint("/designdoc/crud").build();
        MenuItem dataModel = MenuItem.builder().name("Data Model")
                .children(new ArrayList<>(Arrays.asList(crudMatrix))).build();

        return new ArrayList<>(Arrays.asList(dataModel));
    }

    private MenuItem buildFunctionModelItem(List<String> designDocIds) {
        List<MenuItem> children = buildFunctionModelItems(designDocIds);
        return MenuItem.builder().name("Function Model").children(children).build();
    }

    private List<MenuItem> buildFunctionModelItems(List<String> designDocIds) {

        List<MenuItem> functionModelItems = new ArrayList<>();

        designDocIds.stream().forEach((id) -> {
            Matcher matcher = designDocIdpattern.matcher(id);
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

            MenuItem newChild = MenuItem.builder().name(part).build();
            currentItems.add(newChild);
            currentItems = newChild.getChildren();
        }

        return currentItems;
    }

    private MenuItem buildMethodItem(String methodName, String designDocId) {
        return MenuItem.builder().name(methodName).endpoint("/designdoc/function/" + designDocId)
                .build();
    }
}
