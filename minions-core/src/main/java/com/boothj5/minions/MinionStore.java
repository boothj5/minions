package com.boothj5.minions;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class MinionStore {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsRunner.class);
    public static final String MANIFEST_MINIONCLASS = "MinionClass";
    private Map<String, Minion> minions;
    private String minionsDir;

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

    public MinionStore(String minionsDir, int refreshSeconds) {
        this.minionsDir = minionsDir;
        this.minions = new HashMap<>();

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
            minions.clear();
            File minionsDirFile = new File(minionsDir);
            File[] files = minionsDirFile.listFiles();
            URL[] urls = new URL[files.length];
            List<String> minionClasses = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
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
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new MinionsException("Error loading minions.", e);
        }
    }
}
