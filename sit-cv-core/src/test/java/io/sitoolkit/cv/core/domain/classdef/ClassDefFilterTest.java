package io.sitoolkit.cv.core.domain.classdef;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import java.util.Arrays;
import org.junit.Test;

public class ClassDefFilterTest {

  @Test
  public void test() {
    FilterConditionGroup filterConditions = new FilterConditionGroup();
    FilterCondition filterCondition = new FilterCondition();
    filterCondition.setName(".*Controller");
    filterConditions.setInclude(Arrays.asList(filterCondition));
    ClassDef clazz = new ClassDef();
    clazz.setName("SomeDTO");

    assertThat(ClassDefFilter.match(clazz, filterConditions), is(false));
  }

  @Test
  public void withDetailTest() {
    ClassDef controller = new ClassDef();
    controller.setName("TestController");
    ClassDef service = new ClassDef();
    service.setName("TestService");
    ClassDef publisher = new ClassDef();
    publisher.setName("TestPublisher");

    FilterConditionGroup filterConditions = new FilterConditionGroup();
    FilterCondition condition1 =
        JsonUtils.str2obj("{\"name\": \".*Controller\"}", FilterCondition.class);
    FilterCondition condition2 =
        JsonUtils.str2obj(
            "{\"name\": \".*Service\", \"withDetail\": false}", FilterCondition.class);
    filterConditions.setInclude(Arrays.asList(condition1, condition2));

    assertThat(condition1.isWithDetail(), is(true));
    assertThat(condition2.isWithDetail(), is(false));
    assertThat(ClassDefFilter.needsDetail(controller, filterConditions), is(true));
    assertThat(ClassDefFilter.needsDetail(service, filterConditions), is(false));
    assertThat(ClassDefFilter.needsDetail(publisher, filterConditions), is(true));
  }
}
