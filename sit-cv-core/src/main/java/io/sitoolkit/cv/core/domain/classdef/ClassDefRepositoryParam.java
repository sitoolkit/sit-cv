package io.sitoolkit.cv.core.domain.classdef;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassDefRepositoryParam {

    private List<Path> srcDirs = new ArrayList<>();
    private List<Path> binDirs = new ArrayList<>();
    private Path jarList;

}
