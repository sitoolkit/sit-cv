package io.sitoolkit.cv.core.infra.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class SignatureParserTest {

  @Test
  public void testParse() {
    String signature =
        "se.citerus.dddsample.interfaces.booking.web.CargoAdminController.assignItinerary(javax.servlet.http.HttpServletRequest,"
            + " javax.servlet.http.HttpServletResponse,"
            + " se.citerus.dddsample.interfaces.booking.web.RouteAssignmentCommand)";
    SignatureParser parser = SignatureParser.parse(signature);

    assertThat(parser.getPackageName(), is("se.citerus.dddsample.interfaces.booking.web"));
    assertThat(
        parser.getSimpleMedhod(),
        is(
            "CargoAdminController.assignItinerary(HttpServletRequest, HttpServletResponse,"
                + " RouteAssignmentCommand)"));
  }
}
