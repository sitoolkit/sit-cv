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
public class CvJavadocTagTextContent implements CvJavadocTagContent {
    private String content;

    @Override
    public CvJavadocTagTextContent parse(JavadocBlockTag blockTag) {
        return builder().content(blockTag.getContent().toText()).build();
    }
}
