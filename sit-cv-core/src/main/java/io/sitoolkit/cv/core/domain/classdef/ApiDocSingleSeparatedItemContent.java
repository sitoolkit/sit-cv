package io.sitoolkit.cv.core.domain.classdef;

public class ApiDocSingleSeparatedItemContent extends ApiDocContentDef {

    @Override
    public void addItem(String item) {
        if (getItems().size() == 0) {
            getItems().add(item);
        } else {
            getItems().set(0, getItems().get(0) + ", " + item);
        }
    }

}
