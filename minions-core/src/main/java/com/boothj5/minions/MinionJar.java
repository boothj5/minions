package com.boothj5.minions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class MinionJar {
    public static final String MANIFEST_CLASS = "MinionClass";
    private static final String MANIFEST_COMMAND = "MinionCommand" ;

    private final File file;
    private final long timestamp;
    private final String className;
    private final String command;

    public MinionJar(File file) throws IOException {
        this.file = file;
        InputStream in = new FileInputStream(file);
        JarInputStream stream = new JarInputStream(in);
        Manifest manifest = stream.getManifest();

        this.timestamp = file.lastModified();
        this.className = manifest.getMainAttributes().getValue(MANIFEST_CLASS);
        this.command = manifest.getMainAttributes().getValue(MANIFEST_COMMAND);
    }

    public String getName() {
        return file.getName();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public URL getURL() throws MalformedURLException {
        return file.toURI().toURL();
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
