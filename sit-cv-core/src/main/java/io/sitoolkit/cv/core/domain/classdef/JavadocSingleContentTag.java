package io.sitoolkit.cv.core.domain.classdef;

public class JavadocSingleContentTag extends JavadocTagDef {

    @Override
    public void addContent(String content) {
        if (getContents().size() == 0) {
            getContents().add(content);
        }
    }

}
