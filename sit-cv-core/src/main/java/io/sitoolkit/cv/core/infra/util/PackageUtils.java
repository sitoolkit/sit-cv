package io.sitoolkit.cv.core.infra.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

public class PackageUtils {

  public static String getVersion() {
    String version = PackageUtils.class.getPackage().getImplementationVersion();
    if (version != null) {
      return version;
    }

    return getVersionFromPomXml();
  }

  private static String getVersionFromPomXml() {
    try (InputStream is = Files.newInputStream(Paths.get("pom.xml"))) {
      Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
      doc.getDocumentElement().normalize();

      String parentVersion =
          (String)
              XPathFactory.newInstance()
                  .newXPath()
                  .compile("/project/parent/version")
                  .evaluate(doc, XPathConstants.STRING);

      if (StringUtils.isNotEmpty(parentVersion)) {
        return parentVersion;
      }

      return (String)
          XPathFactory.newInstance()
              .newXPath()
              .compile("/project/version")
              .evaluate(doc, XPathConstants.STRING);
    } catch (Exception e) {
      throw new RuntimeException("Get version from pom.xml failed");
    }
  }
}
