package io.sitoolkit.cv.core.domain.project.analyze;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.infra.config.EnclosureFilterCondition;

public class SqlLogListenerTest {

  SqlLogListener listener =
      new SqlLogListener(new EnclosureFilterCondition(".*org.hibernate.SQL.*", "^\\s*[0-9]{4}-.*"));

  @Test
  public void testListen() {
    List<String> logs = new ArrayList<>();
    logs.add("select1");
    logs.add("2019-01-11 17:02:01.934 DEBUG 1740 --- [           main] org.hibernate.SQL  : ");
    logs.add("select2");
    logs.add("[RepositoryMethod]AController.find1(Arg1)");
    logs.add("2019-01-11 17:02:01.934 DEBUG 1740 --- [           main] org.hibernate.SQL  : ");
    logs.add("select3");
    logs.add("2019-01-11 17:02:01.936 DEBUG 1740 --- [           main] org.hibernate.SQL  ");
    logs.add("select4");
    logs.add("[RepositoryMethod]AController.find2(Arg1)");
    logs.add("2019-01-11 17:02:01.934 DEBUG 1740 --- [           main] not.sql.log  : ");
    logs.add("select5");
    logs.add("    [RepositoryMethod]AController.find3(Arg1)");
    logs.add("    2019-01-11 17:02:01.934 DEBUG 1740 --- [           main] org.hibernate.SQL  : ");
    logs.add("    select6");
    logs.add("[RepositoryMethod]AController.find4(Arg1)");
    logs.add("2019-01-11 17:02:01.934 DEBUG 1740 --- [           main] org.hibernate.SQL  : ");
    logs.add("select7");
    logs.add("2019-01-11 17:02:01.934 DEBUG 1740 --- [           main] no.sql  : ");
    logs.add("end");
    logs.forEach((log) -> listener.nextLine(log));

    List<SqlPerMethod> sqlLogs = listener.getSqlLogs();

    assertThat(sqlLogs.size(), is(3));
    assertThat(sqlLogs.get(0).getRepositoryMethod(), is("AController.find1(Arg1)"));
    assertThat(sqlLogs.get(0).getSqlText(), is("select3\n"));
    assertThat(sqlLogs.get(1).getRepositoryMethod(), is("AController.find3(Arg1)"));
    assertThat(sqlLogs.get(1).getSqlText(), is("    select6\n"));
    assertThat(sqlLogs.get(2).getRepositoryMethod(), is("AController.find4(Arg1)"));
    assertThat(sqlLogs.get(2).getSqlText(), is("select7\n"));
  }
}
