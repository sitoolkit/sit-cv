package io.sitoolkit.cv.core.domain.uml.plantuml;

public class IdentiferFormatter {

    boolean showPackageName = false;

    public String format(String identifer) {
        return showPackageName ? identifer : hidePackageName(identifer);
    }

    private String hidePackageName(String identifer) {
        return identifer.replaceAll("[^ .()<>,]+\\.", "");
    }
}
