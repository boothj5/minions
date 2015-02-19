package com.boothj5.minions;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MinionStore {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsRunner.class);
    private final MultiUserChat muc;
    private final Minions minions;
    private final String minionsDirProp;
    private final Map<String, MinionJar> currentJars;
    private boolean loaded;

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

    public MinionStore(String minionsDirProp, int refreshSeconds, MultiUserChat muc) {
        this.minionsDirProp = minionsDirProp;
        this.minions = new Minions();
        this.currentJars = new HashMap<>();
        this.muc = muc;
        this.loaded = false;

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
        return minions.getCommands();
    }

    public Minion get(String command) {
        return minions.get(command);
    }

    private void load() throws MinionsException {
        try {
            Map<String, MinionJar> newJars = new HashMap<>();
            Map<String, MinionJar> jarsToLoad = new HashMap<>();

            List<URL> urlList = new ArrayList<>();
            List<String> jarsToRemove = new ArrayList<>();

            MinionsDir minionsDir = new MinionsDir(minionsDirProp);
            List<MinionJar> newMinionJars = minionsDir.listMinionJars();

            for (MinionJar newMinionJar : newMinionJars) {
                newJars.put(newMinionJar.getName(), newMinionJar);
                if (currentJars.containsKey(newMinionJar.getName())) {
                    MinionJar currentMinionJar = currentJars.get(newMinionJar.getName());
                    if (!currentMinionJar.getTimestamp().equals(newMinionJar.getTimestamp())) {
                        jarsToLoad.put(newMinionJar.getName(), newMinionJar);
                        urlList.add(newMinionJar.getURL());
                        LOG.debug("Updated JAR: " + newMinionJar.getName());
                        if (loaded) {
                            muc.sendMessage("Updated JAR: " + newMinionJar.getName());
                        }
                    }
                } else {
                    jarsToLoad.put(newMinionJar.getName(), newMinionJar);
                    urlList.add(newMinionJar.getURL());
                    LOG.debug("Added JAR: " + newMinionJar.getName());
                    if (loaded) {
                        muc.sendMessage("Added JAR: " + newMinionJar.getName());
                    }
                }
            }

            for (String currentJar : currentJars.keySet()) {
                if (!newJars.containsKey(currentJar)) {
                    minions.remove(currentJars.get(currentJar).getCommand());
                    jarsToRemove.add(currentJar);
                    LOG.debug("Removed JAR: " + currentJar);
                    if (loaded) {
                        muc.sendMessage("Removed JAR: " + currentJar);
                    }
                }
            }

            URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]));

            for (String jarToLoad : jarsToLoad.keySet()) {
                MinionJar minionJarToLoad = jarsToLoad.get(jarToLoad);
                minions.add(minionJarToLoad.getCommand(), minionJarToLoad.loadMinionClass(loader));
            }

            for (String jarToLoad : jarsToLoad.keySet()) {
                currentJars.put(jarToLoad, jarsToLoad.get(jarToLoad));
            }
            for (String jarToRemove : jarsToRemove) {
                currentJars.remove(jarToRemove);
            }

            loaded = true;

        } catch (Throwable t) {
            throw new MinionsException(t);
        }
    }
}