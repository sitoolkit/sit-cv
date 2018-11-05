package io.sitoolkit.cv.core.domain.classdef.javadoc;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum CvJavadocTagType {
    RETURN("return", "Return:", CvJavadocReturnTag.class, CvJavadocTagTextContent.class),
    PARAM("param", "Parameters:", CvJavadocParamTag.class, CvJavadocTagNameTextPairContent.class);

    static Map<String, CvJavadocTagType> map = new HashMap<>();

    static {
        for (CvJavadocTagType type : CvJavadocTagType.values()) {
            map.put(type.tag, type);
        }
    }

    String tag;
    @Getter
    String label;
    @Getter
    Class<? extends CvJavadocTag> clazz;
    @Getter
    Class<? extends CvJavadocTagContent> contentClazz;

    private CvJavadocTagType(String tag, String label, Class<? extends CvJavadocTag> clazz,
            Class<? extends CvJavadocTagContent> contentClazz) {
        this.tag = tag;
        this.label = label;
        this.clazz = clazz;
        this.contentClazz = contentClazz;
    }

    public static CvJavadocTagType getTagType(String tag) {
        return map.get(tag);
    }
}
