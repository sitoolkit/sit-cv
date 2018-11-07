package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.javadoc.JavadocBlockTag;

import io.sitoolkit.cv.core.domain.classdef.JavadocMultipleContentTag;
import io.sitoolkit.cv.core.domain.classdef.JavadocSeeTag;
import io.sitoolkit.cv.core.domain.classdef.JavadocSingleContentTag;
import io.sitoolkit.cv.core.domain.classdef.JavadocTagDef;

public class JavadocParser {
    public static JavadocTagType getTagType(JavadocBlockTag tag) {
        switch (tag.getType()) {
        case RETURN:
            return JavadocTagType.RETURN;
        case DEPRECATED:
            return JavadocTagType.DEPRECATED;
        case SINCE:
            return JavadocTagType.SINCE;
        case PARAM:
            return JavadocTagType.PARAM;
        case EXCEPTION:
        case THROWS:
            return JavadocTagType.THROWS;
        case SEE:
            return JavadocTagType.SEE;
        case VERSION:
        case AUTHOR:
        case SERIAL:
        case SERIAL_DATA:
        case SERIAL_FIELD:
        case UNKNOWN:
            break;
        }
        return null;
    }

    public static JavadocTagDef build(JavadocBlockTag tag) {
        JavadocTagDef tagDef = null;
        switch (tag.getType()) {
        case RETURN:
        case DEPRECATED:
        case SINCE:
            tagDef = new JavadocSingleContentTag();
            break;
        case PARAM:
        case EXCEPTION:
        case THROWS:
            tagDef = new JavadocMultipleContentTag();
            break;
        case SEE:
            tagDef = new JavadocSeeTag();
            break;
        case VERSION:
        case AUTHOR:
        case SERIAL:
        case SERIAL_DATA:
        case SERIAL_FIELD:
        case UNKNOWN:
            return null;
        }
        tagDef.setName(tag.getTagName());
        tagDef.setLabel(tag.getTagName().toUpperCase());

        return tagDef;
    }

    public static String getContent(JavadocBlockTag tag) {
        String content = "";
        switch (tag.getType()) {
        case RETURN:
        case DEPRECATED:
        case SINCE:
            content = tag.getContent().toText();
            break;
        case PARAM:
            content = tag.getName().orElse("") + " - " + tag.getContent();
            break;
        case EXCEPTION:
        case THROWS:
            String[] splitted = tag.getContent().toText().split("\\s", 2);
            content = String.join(" - ", splitted);
            break;
        case SEE:
            content = tag.getContent().toText();
            break;
        case VERSION:
        case AUTHOR:
        case SERIAL:
        case SERIAL_DATA:
        case SERIAL_FIELD:
        case UNKNOWN:
            return null;
        }

        return content;
    }
}
