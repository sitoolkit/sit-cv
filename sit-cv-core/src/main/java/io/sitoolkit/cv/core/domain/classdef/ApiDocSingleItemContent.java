package io.sitoolkit.cv.core.domain.classdef;

public class ApiDocSingleItemContent extends ApiDocContentDef {

    @Override
    public void addItem(String item) {
        if (getItems().size() == 0) {
            getItems().add(item);
        }
    }

}
