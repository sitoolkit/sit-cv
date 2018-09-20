package io.sitoolkit.cv.core.domain.classdef.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDefFilterConditionReader {

    public static Optional<ClassDefFilterCondition> read(Path projectPath) {

        File jsonFile = projectPath.resolve("sit-cv-filter.json").toFile();
        ObjectMapper mapper = new ObjectMapper();

        try {
            ClassDefFilterCondition condition = mapper.readValue(jsonFile, ClassDefFilterCondition.class);
            log.debug("class filter read : file = {} ", jsonFile.getAbsolutePath());
            return Optional.ofNullable(condition);

        } catch (IOException e) {
            log.debug("reading filter file failed : file = {} : exception = {}", jsonFile, e);
            return Optional.empty();
        }
    }
}
