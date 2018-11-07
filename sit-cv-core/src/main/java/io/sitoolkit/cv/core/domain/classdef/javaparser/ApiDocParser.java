package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.javadoc.JavadocBlockTag;

import io.sitoolkit.cv.core.domain.classdef.ApiDocMultipleItemContent;
import io.sitoolkit.cv.core.domain.classdef.ApiDocSingleSeparatedItemContent;
import lombok.extern.slf4j.Slf4j;
import io.sitoolkit.cv.core.domain.classdef.ApiDocSingleItemContent;
import io.sitoolkit.cv.core.domain.classdef.ApiDocContentDef;
import io.sitoolkit.cv.core.domain.classdef.ApiDocContentType;

@Slf4j
public class ApiDocParser {
    public static ApiDocContentType getTagType(JavadocBlockTag.Type tagType) {
        switch (tagType) {
        case RETURN:
            return ApiDocContentType.RETURN;
        case DEPRECATED:
            return ApiDocContentType.DEPRECATED;
        case SINCE:
            return ApiDocContentType.SINCE;
        case PARAM:
            return ApiDocContentType.PARAM;
        case EXCEPTION:
        case THROWS:
            return ApiDocContentType.THROWS;
        case SEE:
            return ApiDocContentType.SEE;
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

    public static ApiDocContentDef buildApiDocContent(JavadocBlockTag tag) {
        ApiDocContentDef contentDef = null;
        switch (tag.getType()) {
        case RETURN:
        case DEPRECATED:
        case SINCE:
            contentDef = new ApiDocSingleItemContent();
            break;
        case PARAM:
        case EXCEPTION:
        case THROWS:
            contentDef = new ApiDocMultipleItemContent();
            break;
        case SEE:
            contentDef = new ApiDocSingleSeparatedItemContent();
            break;
        case VERSION:
        case AUTHOR:
        case SERIAL:
        case SERIAL_DATA:
        case SERIAL_FIELD:
        case UNKNOWN:
            log.warn("Build invalid apidoc: '{}'", tag.toText());
            contentDef = new ApiDocSingleItemContent();
            break;
        }
        contentDef.setName(tag.getTagName());
        contentDef.setLabel(getTagType(tag.getType()).getLabel());

        return contentDef;
    }

    public static String buildApiDocItem(JavadocBlockTag tag) {
        String item = "";
        switch (tag.getType()) {
        case RETURN:
        case DEPRECATED:
        case SINCE:
            item = tag.getContent().toText();
            break;
        case PARAM:
            item = tag.getName().orElse("") + " - " + tag.getContent().toText();
            break;
        case EXCEPTION:
        case THROWS:
            String[] splitted = tag.getContent().toText().split("\\s", 2);
            item = String.join(" - ", splitted);
            break;
        case SEE:
            item = tag.getContent().toText();
            break;
        case VERSION:
        case AUTHOR:
        case SERIAL:
        case SERIAL_DATA:
        case SERIAL_FIELD:
        case UNKNOWN:
            log.warn("Build invalid apidoc item: '{}'", tag.toText());
            item = "";
            break;
        }

        return item;
    }
}
