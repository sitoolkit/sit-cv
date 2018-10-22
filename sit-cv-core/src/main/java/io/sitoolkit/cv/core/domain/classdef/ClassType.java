package io.sitoolkit.cv.core.domain.classdef;

public enum ClassType {
    CLASS, INTERFACE, ENUM;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
