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
    public static final String MANIFEST_MINIONCLASS = "MinionClass";
    private final MultiUserChat muc;
    private final Map<String, Minion> minions;
    private final String minionsDir;
    private final Map<String, Long> jarFiles;

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
        this.jarFiles = new HashMap<>();
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
            Map<String, Long> newJarFiles = new HashMap<>();
            minions.clear();
            File minionsDirFile = new File(minionsDir);
            File[] files = minionsDirFile.listFiles();
            URL[] urls = new URL[files.length];
            List<String> minionClasses = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                newJarFiles.put(files[i].getName(), files[i].lastModified());
                urls[i] = files[i].toURI().toURL();
                InputStream in = new FileInputStream(files[i]);
                JarInputStream stream = new JarInputStream(in);
                Manifest manifest = stream.getManifest();
                minionClasses.add(manifest.getMainAttributes().getValue(MANIFEST_MINIONCLASS));
            }

            URLClassLoader loader = new URLClassLoader(urls);

            for (String minionClass : minionClasses) {
                Class<?> clazz = Class.forName(minionClass, true, loader);
                Class<? extends Minion> minionClazz = clazz.asSubclass(Minion.class);
                Constructor<? extends Minion> ctor = minionClazz.getConstructor();
                Minion minion = ctor.newInstance();
                minions.put(minion.getCommand(), minion);
            }

            Map<String, Long> toUpdate = new HashMap<>();
            Map<String, Long> toAdd = new HashMap<>();
            for (String newJarFile : newJarFiles.keySet()) {
                if (jarFiles.containsKey(newJarFile)) {
                    Long timestamp = jarFiles.get(newJarFile);
                    if (!timestamp.equals(newJarFiles.get(newJarFile))) {
                        toUpdate.put(newJarFile, newJarFiles.get(newJarFile));
                        LOG.debug("Updated JAR: " + newJarFile);
                        muc.sendMessage("Updated JAR: " + newJarFile);
                    }
                } else {
                    toAdd.put(newJarFile, newJarFiles.get(newJarFile));
                    LOG.debug("Added JAR: " + newJarFile);
                    muc.sendMessage("Added JAR: " + newJarFile);
                }
            }

            List<String> toRemove = new ArrayList<>();
            for (String jarFile : jarFiles.keySet()) {
                if (!newJarFiles.containsKey(jarFile)) {
                    toRemove.add(jarFile);
                    LOG.debug("Removed JAR: " + jarFile);
                    muc.sendMessage("Removed JAR: " + jarFile);
                }
            }

            for (String update : toUpdate.keySet()) {
                jarFiles.put(update, toUpdate.get(update));
            }
            for (String add : toAdd.keySet()) {
                jarFiles.put(add, toAdd.get(add));
            }
            for (String removeFile : toRemove) {
                jarFiles.remove(removeFile);
            }

        } catch (Throwable t) {
            throw new MinionsException(t);
        }
    }
}
