package io.sitoolkit.cv.core.infra.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassLoaderUtils {

  public static boolean addPathToClasspath(Path path) {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    try {
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
      method.setAccessible(true);
      method.invoke(classLoader, new Object[] {path.toUri().toURL()});

      log.info("Added to classpath : {}", path);
      return true;

    } catch (NoSuchMethodException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | MalformedURLException e) {
      return false;
    }
  }
}
