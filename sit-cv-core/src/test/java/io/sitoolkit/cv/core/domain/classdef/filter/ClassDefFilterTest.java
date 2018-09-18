package io.sitoolkit.cv.core.domain.classdef.filter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;

public class ClassDefFilterTest {

    final List<String> patterns = Arrays.asList(
            "java..*",
            "a.b.c.Head*",
            "a.b.c.*Tail",
            "a.b.c.*Middle*",
            "a.b.c.Abc*xyz",
            "b.*middle*.c.*",
            "b..d.*",
            "b.e..*"
            );

    @Before
    public void setUp() {

    }

    @Test
    public void testToRegex() {
        final List<String> expected = Arrays.asList(
                "java\\.(.*\\.)?[^.]*",
                "a\\.b\\.c\\.Head[^.]*",
                "a\\.b\\.c\\.[^.]*Tail",
                "a\\.b\\.c\\.[^.]*Middle[^.]*",
                "a\\.b\\.c\\.Abc[^.]*xyz",
                "b\\.[^.]*middle[^.]*\\.c\\.[^.]*",
                "b\\.(.*\\.)?d\\.[^.]*",
                "b\\.e\\.(.*\\.)?[^.]*"
                );

        ClassDefFilter target = new ClassDefFilter();

        IntStream.range(0, patterns.size()).forEachOrdered(i -> {
            assertThat(target.toRegex(patterns.get(i)), is(expected.get(i)));
        });
    }

    @Test
    public void testFillter() {
        ClassDefFilterCondition condition = new ClassDefFilterCondition();
        condition.getTypes().addAll(patterns);

        List<ClassDef> classList = new ArrayList<>();
        classList.add(createCD("java.util.regex", "Pattern")); //match "java..*"
        classList.add(createCD("a.b.c", "HeadXXX")); //match "a.b.c.Head*"
        classList.add(createCD("a.b.c", "XXXTail")); //match "a.b.c.*Tail"
        classList.add(createCD("a.b.c", "XXXMiddleXXX")); //match "a.b.c.*Middle*"
        classList.add(createCD("a.b.c", "AbcXXXxyz")); //match "a.b.c.Abc*xyz"
        classList.add(createCD("a.b.c", "TailHeadAbcxyz")); //no match
        classList.add(createCD("b", "HeadXXX")); //no match
        classList.add(createCD("b.xxxmiddlexxx.c", "Data")); // match "b.*middle*.c.*"
        classList.add(createCD("b.d", "Data")); // match "b..d.*"
        classList.add(createCD("b.xxx.xxx.d", "Data")); // match "b..d.*"
        classList.add(createCD("b.xxx.xxx.e", "Data")); //no match
        classList.add(createCD("b.d.xxx.xxx", "Data")); //no match
        classList.add(createCD("b.e", "Data")); //match "b.e..*"
        classList.add(createCD("b.e.xxx", "Data")); //match "b.e..*"
        classList.add(createCD("b.e.xxx.xxx", "Data")); //match "b.e..*"

        List<ClassDef> expected = new ArrayList<>();
        expected.add(createCD("java.util.regex", "Pattern")); //match "java..*"
        expected.add(createCD("a.b.c", "HeadXXX")); //match "a.b.c.Head*"
        expected.add(createCD("a.b.c", "XXXTail")); //match "a.b.c.*Tail"
        expected.add(createCD("a.b.c", "XXXMiddleXXX")); //match "a.b.c.*Middle*"
        expected.add(createCD("a.b.c", "AbcXXXxyz")); //match "a.b.c.Abc*xyz"
        expected.add(createCD("b.xxxmiddlexxx.c", "Data")); // match "b.*middle*.c.*"
        expected.add(createCD("b.d", "Data")); //match "b..d.*"
        expected.add(createCD("b.xxx.xxx.d", "Data")); // match "b..d.*"
        expected.add(createCD("b.e", "Data")); //match "b.e..*"
        expected.add(createCD("b.e.xxx", "Data")); //match "b.e..*"
        expected.add(createCD("b.e.xxx.xxx", "Data")); //match "b.e..*"

        ClassDefFilter target = new ClassDefFilter();
        target.setCondition(condition);
        List<ClassDef> actual = classList.stream().filter(target).collect(Collectors.toList());
        IntStream.range(0, actual.size()).forEachOrdered(i -> {
            assertThat(actual.get(i), is(expected.get(i)));
        });
        assertThat(actual, is(expected));
    }

    @Test
    public void testFillterNoCondition() {
        ClassDefFilter target = new ClassDefFilter();

        List<ClassDef> classList = new ArrayList<>();
        classList.add(createCD("foo.util.regex", "Foobar")); //match
        classList.add(createCD("a.b.c", "XXX")); //match

        List<ClassDef> actual = classList.stream().filter(target).collect(Collectors.toList());
        assertThat(actual.size(), is(classList.size()));
    }

    private ClassDef createCD(String pkg, String name) {
        ClassDef cd = new ClassDef();
        cd.setPkg(pkg);
        cd.setName(name);
        return cd;
    }

}
