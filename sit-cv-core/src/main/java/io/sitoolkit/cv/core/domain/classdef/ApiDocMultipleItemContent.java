package io.sitoolkit.cv.core.domain.classdef;

public class ApiDocMultipleItemContent extends ApiDocContentDef {

    @Override
    public void addItem(String item) {
        getItems().add(item);
    }

}
