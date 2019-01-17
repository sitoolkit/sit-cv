package io.sitoolkit.cv.core.domain.data;

public enum CrudType {
    CREATE, REFERENCE, UPDATE, DELETE, MERGE, NA;

    @Override
    public String toString() {
        return name().substring(0, 1);
    }

}
