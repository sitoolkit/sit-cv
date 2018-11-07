package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.javadoc.JavadocBlockTag;

import io.sitoolkit.cv.core.domain.classdef.ApiDocContentType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavadocParser {
    private Map<JavadocBlockTag.Type, ApiDocContentType> typeMap = new HashMap<>();

    public JavadocParser() {
        typeMap.put(JavadocBlockTag.Type.DEPRECATED, ApiDocContentType.DEPRECATED);
        typeMap.put(JavadocBlockTag.Type.RETURN, ApiDocContentType.RETURN);
        typeMap.put(JavadocBlockTag.Type.SINCE, ApiDocContentType.SINCE);
        typeMap.put(JavadocBlockTag.Type.PARAM, ApiDocContentType.PARAM);
        typeMap.put(JavadocBlockTag.Type.EXCEPTION, ApiDocContentType.THROWS);
        typeMap.put(JavadocBlockTag.Type.THROWS, ApiDocContentType.THROWS);
        typeMap.put(JavadocBlockTag.Type.SEE, ApiDocContentType.SEE);
    }

    public ApiDocContentType getApiDocContentType(JavadocBlockTag.Type tagType) {
        return typeMap.get(tagType);
    }

    public String buildApiDocContentItem(JavadocBlockTag tag) {
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
