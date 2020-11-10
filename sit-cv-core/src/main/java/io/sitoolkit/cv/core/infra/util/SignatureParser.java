package io.sitoolkit.cv.core.infra.util;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SignatureParser {

  private String packageName;
  /** class + method + params without package */
  private String simpleMedhod;

  public static SignatureParser parse(String signature) {
    SignatureParser parser = new SignatureParser();

    int leftRoundBracketsIndex = signature.indexOf("(");
    int methodStartPeriodIndex = signature.substring(0, leftRoundBracketsIndex).lastIndexOf(".");
    int classStartPeriodIndex = signature.substring(0, methodStartPeriodIndex).lastIndexOf(".");

    parser.setPackageName(signature.substring(0, classStartPeriodIndex));

    String methodParam = signature.substring(leftRoundBracketsIndex, signature.length());

    StringBuilder simpleMedhod = new StringBuilder();

    String classAndMedhod =
        signature.substring(classStartPeriodIndex + 1, leftRoundBracketsIndex + 1);
    simpleMedhod.append(classAndMedhod);

    for (String param : methodParam.split(",")) {
      simpleMedhod.append(StringUtils.substringAfterLast(param, "."));

      if (!param.endsWith(")")) {
        simpleMedhod.append(", ");
      }
    }

    parser.setSimpleMedhod(simpleMedhod.toString());

    return parser;
  }
}
