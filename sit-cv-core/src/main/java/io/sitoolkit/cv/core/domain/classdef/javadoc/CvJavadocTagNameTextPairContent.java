package io.sitoolkit.cv.core.domain.classdef.javadoc;

import com.github.javaparser.javadoc.JavadocBlockTag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvJavadocTagNameTextPairContent implements CvJavadocTagContent {
    private String name;
    private String text;

    @Override
    public CvJavadocTagNameTextPairContent parse(JavadocBlockTag blockTag) {
        return builder().name(blockTag.getName().orElse("")).text(blockTag.getContent().toText())
                .build();
    }
}
