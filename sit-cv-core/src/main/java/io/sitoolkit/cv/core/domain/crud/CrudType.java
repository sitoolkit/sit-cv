package io.sitoolkit.cv.core.domain.crud;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CrudType {
    CREATE, REFERENCE, UPDATE, DELETE, MERGE;

    @JsonValue
    @Override
    public String toString() {
        return name().substring(0, 1);
    }

}
