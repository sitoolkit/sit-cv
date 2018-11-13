/*
 * MIT License
 *
 * Copyright (c) 2010 Anthony Whitford
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * https://github.com/awhitford/lombok.maven/blob/master/lombok-maven-plugin/src/main/java/lombok/launch/Delombok.java
 */
package lombok.launch;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Since the Shadow Class Loader hides Lombok's internal Delombok, we need to access it via reflection.
 *
 * @see <a href="https://github.com/rzwitserloot/lombok/blob/master/src/delombok/lombok/delombok/Delombok.java">lombok.delombok.Delombok</a>
 */
public class Delombok {

    private final Object delombokInstance;

    private final Method addDirectory;
    private final Method delombok;
    private final Method formatOptionsToMap;
    private final Method setVerbose;
    private final Method setCharset;
    private final Method setClasspath;
    private final Method setFormatPreferences;
    private final Method setOutput;
    private final Method setSourcepath;

    public Delombok () throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        final ClassLoader shadowClassLoader = Main.createShadowClassLoader();
        final Class<?> delombokClass = shadowClassLoader.loadClass("lombok.delombok.Delombok");
        this.delombokInstance = delombokClass.newInstance();

        // Get method handles...
        this.addDirectory = delombokClass.getMethod("addDirectory", File.class);
        this.delombok = delombokClass.getMethod("delombok");
        this.formatOptionsToMap = delombokClass.getMethod("formatOptionsToMap", List.class);
        this.setVerbose = delombokClass.getMethod("setVerbose", boolean.class);
        this.setCharset = delombokClass.getMethod("setCharset", String.class);
        this.setClasspath = delombokClass.getMethod("setClasspath", String.class);
        this.setFormatPreferences = delombokClass.getMethod("setFormatPreferences", Map.class);
        this.setOutput = delombokClass.getMethod("setOutput", File.class);
        this.setSourcepath = delombokClass.getMethod("setSourcepath", String.class);
    }

    public void addDirectory (final File base) throws IllegalAccessException, IOException, InvocationTargetException {
        addDirectory.invoke(delombokInstance, base);
    }

    public boolean delombok () throws IllegalAccessException, IOException, InvocationTargetException {
        return Boolean.parseBoolean( delombok.invoke(delombokInstance).toString() );
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> formatOptionsToMap (final List<String> formatOptions) throws Exception {
        return (Map<String, String>)formatOptionsToMap.invoke(null, formatOptions);
    }

    public void setVerbose (final boolean verbose) throws IllegalAccessException, InvocationTargetException {
        setVerbose.invoke(delombokInstance, verbose);
    }

    public void setCharset (final String charset) throws IllegalAccessException, InvocationTargetException {
        setCharset.invoke(delombokInstance, charset);
    }

    public void setClasspath (final String classpath) throws IllegalAccessException, InvocationTargetException {
        setClasspath.invoke(delombokInstance, classpath);
    }

    public void setFormatPreferences (final Map<String, String> prefs) throws IllegalAccessException, InvocationTargetException {
        setFormatPreferences.invoke(delombokInstance, prefs);
    }

    public void setOutput (final File dir) throws IllegalAccessException, InvocationTargetException {
        setOutput.invoke(delombokInstance, dir);
    }

    public void setSourcepath (final String sourcepath) throws IllegalAccessException, InvocationTargetException {
        setSourcepath.invoke(delombokInstance, sourcepath);
    }
}
