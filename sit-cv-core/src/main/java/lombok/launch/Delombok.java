package lombok.launch;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Since the Shadow Class Loader hides Lombok's internal Delombok, we need to
 * access it via reflection.
 *
 * @see <a href=
 *      "https://github.com/rzwitserloot/lombok/blob/master/src/delombok/lombok/delombok/Delombok.java">lombok.delombok.Delombok</a>
 */
public class Delombok {

    private final Object delombokInstance;

    private final Method addDirectory;
    private final Method delombok;
    private final Method setCharset;
    private final Method setClasspath;
    private final Method setOutput;
    private final Method setSourcepath;

    public Delombok() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, SecurityException, InvocationTargetException {
        final ClassLoader shadowClassLoader = Main.getShadowClassLoader();
        final Class<?> delombokClass = shadowClassLoader.loadClass("lombok.delombok.Delombok");
        this.delombokInstance = delombokClass.getDeclaredConstructor().newInstance();
        this.addDirectory = delombokClass.getMethod("addDirectory", File.class);
        this.delombok = delombokClass.getMethod("delombok");
        this.setCharset = delombokClass.getMethod("setCharset", String.class);
        this.setClasspath = delombokClass.getMethod("setClasspath", String.class);
        this.setOutput = delombokClass.getMethod("setOutput", File.class);
        this.setSourcepath = delombokClass.getMethod("setSourcepath", String.class);
    }

    public void addDirectory(final File base)
            throws IllegalAccessException, IOException, InvocationTargetException {
        addDirectory.invoke(delombokInstance, base);
    }

    public boolean delombok()
            throws IllegalAccessException, IOException, InvocationTargetException {
        return Boolean.parseBoolean(delombok.invoke(delombokInstance).toString());
    }

    public void setCharset(final String charset)
            throws IllegalAccessException, InvocationTargetException {
        setCharset.invoke(delombokInstance, charset);
    }

    public void setClasspath(final String classpath)
            throws IllegalAccessException, InvocationTargetException {
        setClasspath.invoke(delombokInstance, classpath);
    }

    public void setOutput(final File dir) throws IllegalAccessException, InvocationTargetException {
        setOutput.invoke(delombokInstance, dir);
    }

    public void setSourcepath(final String sourcepath)
            throws IllegalAccessException, InvocationTargetException {
        setSourcepath.invoke(delombokInstance, sourcepath);
    }
}
