package io.sitoolkit.cv.core.infra.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CsvUtils {

    private CsvUtils() {
    }

    public static <T> void bean2csv(List<T> beans, Path csvFilePath) {

        try (Writer writer = new FileWriter(csvFilePath.toFile())) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).build();
            beanToCsv.write(beans);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
