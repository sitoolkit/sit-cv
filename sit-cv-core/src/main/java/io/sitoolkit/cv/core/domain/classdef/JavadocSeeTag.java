package io.sitoolkit.cv.core.domain.classdef;

public class JavadocSeeTag extends JavadocTagDef {

    @Override
    public void addContent(String content) {
        if (getContents().size() == 0) {
            getContents().add(content);
        } else {
            getContents().set(0, getContents().get(0) + ", " + content);
        }
    }

}
