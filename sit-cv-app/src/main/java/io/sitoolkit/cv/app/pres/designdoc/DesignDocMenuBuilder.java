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

    public List<MenuItem> build(List<String> designDocIds) {

        List<MenuItem> menuItems = buildStaticItems();
        menuItems.add(buildFunctionModelItem(designDocIds));

        return menuItems;
    }

    private List<MenuItem> buildStaticItems() {
        MenuItem crudMatrix = MenuItem.builder().name("CRUD Matrix").endpoint("/designdoc/crud")
                .build();
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

            String[] signatureParts = classSignature.split("\\.");

            List<MenuItem> currentItems = functionModelItems;
            for (String part : signatureParts) {
                Optional<MenuItem> partItem = findPartItem(currentItems, part);

                if (partItem.isPresent()) {
                    currentItems = partItem.get().getChildren();
                } else {
                    MenuItem newPartItem = MenuItem.builder().name(part).build();
                    currentItems.add(newPartItem);
                    currentItems = newPartItem.getChildren();
                }
            }

            currentItems.add(buildMethodItem(methodName, id));
        });

        return functionModelItems;
    }

    private Optional<MenuItem> findPartItem(List<MenuItem> items, String part) {
        return items.stream().filter((c) -> c.getName().equals(part)).findAny();
    }

    private MenuItem buildMethodItem(String methodName, String designDocId) {
        return MenuItem.builder().name(methodName).endpoint("/designdoc/function/" + designDocId)
                .build();
    }
}
