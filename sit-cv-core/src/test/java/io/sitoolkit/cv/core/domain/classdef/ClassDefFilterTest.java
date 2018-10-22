package io.sitoolkit.cv.core.domain.classdef;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;

public class ClassDefFilterTest {

    @Test
    public void test() {
        FilterConditionGroup filterConditions = new FilterConditionGroup();
        filterConditions
                .setInclude(Arrays.asList(FilterCondition.builder().name(".*Controller").build()));
        ClassDef clazz = new ClassDef();
        clazz.setName("SomeDTO");

        assertThat(ClassDefFilter.match(clazz, filterConditions), is(false));
    }

}
