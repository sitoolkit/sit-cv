package a.b.c.domain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class TestBase {

  @BeforeClass
  public static void setUpBeforeClass() throws IOException, SQLException {
    Path derbyDir = Paths.get("derby/testdb");
    if (Files.exists(derbyDir)) {
      try (Stream<Path> paths = Files.walk(derbyDir)) {
        paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      }
    }
    executeSql(Paths.get("tools/db/initdb.sql"));
  }

  @AfterClass
  public static void tearDownAfterClass() throws IOException, SQLException {
    executeSql(Paths.get("tools/db/dropdb.sql"));
  }

  private static void executeSql(Path sqlPath) throws IOException, SQLException {
    Connection connection =
        DriverManager.getConnection(
            "jdbc:derby://localhost:1527/testdb;create=true;user=derby;password=derby");
    Statement state = connection.createStatement();

    String script = FileUtils.readFileToString(sqlPath.toFile(), Charset.forName("UTF-8"));
    for (String queqy : script.split(";")) {
      if (!queqy.isEmpty()) {
        state.addBatch(queqy);
      }
    }
    state.executeBatch();
    connection.close();
  }
}
