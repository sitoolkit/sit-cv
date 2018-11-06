package io.sitoolkit.cv.core.domain.classdef;

public class JavadocSingleContentTag extends JavadocTagDef {

    public void addContent(String source) {
        if (getContents().size() == 0) {
            getContents().add(source);
        }
    }

}
