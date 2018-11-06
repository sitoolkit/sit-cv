package io.sitoolkit.cv.core.domain.classdef.javadoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.javadoc.Javadoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class CvJavadoc {
    private String qualifiedClassName;
    private String methodDeclaration;
    private String description;
    private CvJavadocTag deprecated;
    private List<CvJavadocTag> tags;

    public static CvJavadoc parse(String qualifiedClassName, String methodDeclaration, Javadoc javadoc) {
        CvJavadocBuilder builder = builder();
        builder = builder.qualifiedClassName(qualifiedClassName)
                .methodDeclaration(methodDeclaration)
                .description(javadoc.getDescription().toText());

        Map<String, CvJavadocTag> tags = new HashMap<>();
        javadoc.getBlockTags().stream().forEach((tag) -> {

            String tagName = tag.getTagName();
            CvJavadocTagType tagType = CvJavadocTagType.getTagType(tagName);
            CvJavadocTag cvTag = tags.computeIfAbsent(tagName, (name) -> {
                try {
                    CvJavadocTag newTag = tagType.getClazz().getDeclaredConstructor().newInstance();
                    newTag.setName(name);
                    return tagType.getClazz().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.debug("parse error: tag {}", name, e);
                    return null;
                }
            });
            if(cvTag != null) {
                cvTag.setLabel(tagType.getLabel());
                try {
                    CvJavadocTagContent content = tagType.getContentClazz().getDeclaredConstructor().newInstance();
                    cvTag.getContents().add(content.parse(tag));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return builder.tags(new ArrayList<CvJavadocTag>(tags.values())).build();
    }
}
