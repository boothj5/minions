package com.boothj5.minions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class MinionJar {
    public static final String MANIFEST_CLASS = "MinionClass";
    private static final String MANIFEST_COMMAND = "MinionCommand" ;

    private final String name;
    private final long timestamp;
    private final URL url;
    private final String command;
    private final String className;

    public MinionJar(File file) throws IOException {
        name = file.getName();
        timestamp = file.lastModified();
        url = file.toURI().toURL();
        InputStream in = new FileInputStream(file);
        JarInputStream stream = new JarInputStream(in);
        Manifest manifest = stream.getManifest();
        command = manifest.getMainAttributes().getValue(MANIFEST_COMMAND);
        className = manifest.getMainAttributes().getValue(MANIFEST_CLASS);
        stream.close();
        in.close();
    }

    public String getName() {
        return name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public URL getURL() {
        return url;
    }

    public String getCommand() {
        return command;
    }

    public Minion loadMinionClass(URLClassLoader loader)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName(className, true, loader);
        Class<? extends Minion> minionClazz = clazz.asSubclass(Minion.class);
        Constructor<? extends Minion> ctr = minionClazz.getConstructor();
        return ctr.newInstance();
    }
}
