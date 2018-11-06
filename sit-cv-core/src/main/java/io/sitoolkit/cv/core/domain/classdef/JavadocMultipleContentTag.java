package io.sitoolkit.cv.core.domain.classdef;

public class JavadocMultipleContentTag extends JavadocTagDef {

    public void addContent(String name, String content) {
        getContents().add(name + " - " + content);
    }

}
