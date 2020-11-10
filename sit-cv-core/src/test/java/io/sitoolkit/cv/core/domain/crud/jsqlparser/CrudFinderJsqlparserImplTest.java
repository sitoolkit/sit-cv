package io.sitoolkit.cv.core.domain.crud.jsqlparser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.cv.core.domain.crud.CrudFindResult;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class CrudFinderJsqlparserImplTest {

  CrudFinderJsqlparserImpl finder = new CrudFinderJsqlparserImpl();

  Set<String> toSet(String... cruds) {
    return Stream.of(cruds).collect(Collectors.toSet());
  }

  Set<String> getCruds(CrudFindResult tableCrud, String table) {
    return tableCrud.getMap().getOrDefault(table, new HashSet<>()).stream()
        .map(crud -> crud.toString())
        .collect(Collectors.toSet());
  }

  @Test
  public void testInsertSelect() {
    CrudFindResult tableCrud = finder.findCrud("INSERT INTO tab_1 (col_1) SELECT col_1 FROM tab_2");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("C")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("R")));

    tableCrud = finder.findCrud("SELECT col_1 INTO tab_2 FROM tab_1");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("R")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("C")));
  }

  @Test
  public void testSelectJoin() {
    CrudFindResult tableCrud =
        finder.findCrud("SELECT * FROM tab_1 t1 JOIN tab_2 t2 ON t1.col_1 = t2.col_2");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("R")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("R")));
  }

  @Test
  public void testSelectSub() {
    CrudFindResult tableCrud =
        finder.findCrud(
            "SELECT * FROM tab_1 t1 WHERE EXISTS (SELECT 1 FROM tab_2 t2 WHERE t2.col_1 ="
                + " t1.col_1)");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("R")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("R")));
  }

  @Test
  public void testSelectWith() {
    CrudFindResult tableCrud =
        finder.findCrud("WITH w1 AS (SELECT * FROM tab_1 t1) SELECT * FROM w1");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("R")));
  }

  @Test
  public void testUpdateSelect() {
    CrudFindResult tableCrud =
        finder.findCrud(
            "UPDATE tab_1 t1 SET col_1 = (SELECT col_1 FROM tab_2 t2 WHERE t2.col_2 = t1.col_2)");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("U")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("R")));

    tableCrud =
        finder.findCrud(
            "UPDATE tab_1 t1 SET col_1 = 'x' WHERE EXISTS (SELECT 1 FROM tab_2 t2 WHERE t2.col_2 ="
                + " t1.col_2)");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("U")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("R")));
  }

  @Test
  public void testMerge() {
    CrudFindResult tableCrud =
        finder.findCrud(
            "MERGE INTO tab_1 t1 USING (SELECT col_1 FROM tab_2) t2 ON (t1.col_1 = t2.col_1) WHEN"
                + " MATCHED THEN UPDATE SET col_2 = 1");

    assertThat(getCruds(tableCrud, "tab_1"), is(toSet("C", "U")));
    assertThat(getCruds(tableCrud, "tab_2"), is(toSet("R")));
  }
}
