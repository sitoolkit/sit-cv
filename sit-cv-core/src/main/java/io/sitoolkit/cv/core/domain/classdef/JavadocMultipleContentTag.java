package io.sitoolkit.cv.core.domain.classdef;

public class JavadocMultipleContentTag extends JavadocTagDef {

    @Override
    public void addContent(String content) {
        getContents().add(content);
    }

}
