package io.sitoolkit.cv.core.domain.uml;

import java.util.Map;
import java.util.Set;

import io.sitoolkit.cv.core.domain.classdef.javadoc.CvJavadoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SequenceDiagram implements DiagramModel{
    private LifeLineDef entryLifeLine;

    @Override
    public String getId() {
        return entryLifeLine.getEntryMessage();
    }

    @Override
    public Set<String> getAllTags() {
        return entryLifeLine.getAllSourceIds();
    }

    @Override
    public Map<String, String> getAllComments() {
        return entryLifeLine.getCommentsRecursively();
    }

    @Override
    public Map<String, CvJavadoc> getAllJavadocs() {
        return entryLifeLine.getJavadocsRecursively();
    }

    @Override
    public Set<String> getAllSourceIds() {
        return entryLifeLine.getAllSourceIds();
    }
}
