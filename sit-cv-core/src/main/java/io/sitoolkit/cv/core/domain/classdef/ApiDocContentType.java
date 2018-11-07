package io.sitoolkit.cv.core.domain.classdef;

import io.sitoolkit.cv.core.infra.resource.MessageManager;
import lombok.Getter;

public enum ApiDocContentType {
    DEPRECATED,
    PARAM,
    RETURN,
    THROWS,
    SINCE,
    SEE;

    @Getter
    private final String label;

    private ApiDocContentType() {
        label = MessageManager.getMessage("classdef.apidoc.label." + this.toString());
    }
}
