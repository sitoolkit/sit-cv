package io.sitoolkit.cv.core.domain.classdef;

public class ApiDocContentBuilder {
    public ApiDocContentDef build(ApiDocContentType type) {
        ApiDocContentDef contentDef = null;
        switch(type) {
        case DEPRECATED:
        case RETURN:
        case SINCE:
            contentDef = new ApiDocSingleItemContent();
            break;
        case PARAM:
        case THROWS:
            contentDef = new ApiDocMultipleItemContent();
            break;
        case SEE:
            contentDef = new ApiDocSingleSeparatedItemContent();
            break;
        }
        contentDef.setLabel(type.getLabel());
        return contentDef;
    }
}
