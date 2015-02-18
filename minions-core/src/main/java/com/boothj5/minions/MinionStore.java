package com.boothj5.minions;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class MinionStore {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsRunner.class);
    public static final String MANIFEST_CLASS = "MinionClass";
    private static final String MANIFEST_COMMAND = "MinionCommand" ;
    private final MultiUserChat muc;
    private final Map<String, Minion> minions;
    private final String minionsDir;
    private final Map<String, JarMetadata> currentJars;

    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException {
        while(isLocked){
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }

    public MinionStore(String minionsDir, int refreshSeconds, MultiUserChat muc) {
        this.minionsDir = minionsDir;
        this.minions = new HashMap<>();
        this.currentJars = new HashMap<>();
        this.muc = muc;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    lock();
                    load();
                    unlock();
                } catch (MinionsException | InterruptedException e) {
                    LOG.error("Error loading minions.", e);
                    e.printStackTrace();
                }
            }
        }, 0, refreshSeconds, TimeUnit.SECONDS);
    }

    public List<String> commandList() {
        List<String> result = new ArrayList<>();
        Set<String> commandSet = minions.keySet();

        for (String command: commandSet) {
            result.add(command);
        }

        return result;
    }

    public Minion get(String command) {
        return minions.get(command);
    }

    private void load() throws MinionsException {
        try {
            File minionsDirFile = new File(minionsDir);
            File[] newJarFiles = minionsDirFile.listFiles();
            Map<String, JarMetadata> newJars = new HashMap<>();

            if (newJarFiles != null) {
                List<URL> urlList = new ArrayList<>();

                Map<String, JarMetadata> jarsToLoad = new HashMap<>();
                List<String> jarsToRemove = new ArrayList<>();

                for (File newJarFile : newJarFiles) {
                    InputStream in = new FileInputStream(newJarFile);
                    JarInputStream stream = new JarInputStream(in);
                    Manifest manifest = stream.getManifest();

                    long timestamp = newJarFile.lastModified();
                    String className = manifest.getMainAttributes().getValue(MANIFEST_CLASS);
                    String command = manifest.getMainAttributes().getValue(MANIFEST_COMMAND);

                    JarMetadata newJar = new JarMetadata(timestamp, command, className);
                    newJars.put(newJarFile.getName(), newJar);

                    if (currentJars.containsKey(newJarFile.getName())) {

                        JarMetadata currentJar = currentJars.get(newJarFile.getName());
                        if (!currentJar.getTimestamp().equals(newJar.getTimestamp())) {
                            jarsToLoad.put(newJarFile.getName(), newJar);
                            urlList.add(newJarFile.toURI().toURL());
                            LOG.debug("Updated JAR: " + newJarFile.getName());
                            muc.sendMessage("Updated JAR: " + newJarFile.getName());
                        }
                    } else {
                        jarsToLoad.put(newJarFile.getName(), newJar);
                        urlList.add(newJarFile.toURI().toURL());
                        LOG.debug("Added JAR: " + newJarFile.getName());
                        muc.sendMessage("Added JAR: " + newJarFile.getName());
                    }
                }

                for (String jarFile : currentJars.keySet()) {
                    if (!newJars.containsKey(jarFile)) {
                        String command = currentJars.get(jarFile).getCommand();
                        Minion minionToRemove = minions.get(command);
                        minionToRemove.onRemove();
                        minions.remove(command);
                        jarsToRemove.add(jarFile);
                        LOG.debug("Removed JAR: " + jarFile);
                        muc.sendMessage("Removed JAR: " + jarFile);
                    }
                }

                URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]));

                for (String jarToLoad : jarsToLoad.keySet()) {
                    String minionClass = newJars.get(jarToLoad).getClassName();
                    String minionCommand = newJars.get(jarToLoad).getCommand();
                    Class<?> clazz = Class.forName(minionClass, true, loader);
                    Class<? extends Minion> minionClazz = clazz.asSubclass(Minion.class);
                    Constructor<? extends Minion> ctr = minionClazz.getConstructor();
                    Minion minion = ctr.newInstance();
                    minions.put(minionCommand, minion);
                }

                for (String addJar : jarsToLoad.keySet()) {
                    currentJars.put(addJar, jarsToLoad.get(addJar));
                }
                for (String removeJar : jarsToRemove) {
                    currentJars.remove(removeJar);
                }
            }

        } catch (Throwable t) {
            throw new MinionsException(t);
        }
    }
}
