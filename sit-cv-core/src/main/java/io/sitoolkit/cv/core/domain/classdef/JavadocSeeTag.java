package io.sitoolkit.cv.core.domain.classdef;

public class JavadocSeeTag extends JavadocTagDef {

    public void addContent(String source) {
        if (getContents().size() == 0) {
            getContents().add(source);
        } else {
            getContents().set(0, getContents().get(0) + ", " + source);
        }
    }

}
