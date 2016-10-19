/*
 * Copyright 2015 - 2016 James Booth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

class MinionStore {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsRunner.class);
    private final MultiUserChat muc;
    private final Minions minions;
    private final String minionsDirProp;
    private final Map<String, MinionJar> currentJars;
    private boolean loaded;

    private boolean isLocked = false;

    synchronized void lock() throws InterruptedException {
        while(isLocked){
            wait();
        }
        isLocked = true;
    }

    synchronized void unlock() {
        isLocked = false;
        notify();
    }

    MinionStore(String minionsDirProp, int refreshSeconds, MultiUserChat muc) {
        this.minionsDirProp = minionsDirProp;
        this.minions = new Minions();
        this.currentJars = new HashMap<>();
        this.muc = muc;
        this.loaded = false;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                lock();
                load();
                unlock();
            } catch (MinionsException | InterruptedException e) {
                LOG.error("Error loading minions.", e);
                e.printStackTrace();
            }
        }, 0, refreshSeconds, TimeUnit.SECONDS);
    }

    List<String> commandList() {
        return minions.getCommands();
    }

    Minion get(String command) {
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
            jarsToRemove.forEach(currentJars::remove);

            loaded = true;

        } catch (Throwable t) {
            throw new MinionsException(t);
        }
    }

    List<MinionJar> getJars() {
        List<MinionJar> result = new ArrayList<>();
        result.addAll(currentJars.values());

        return result;
    }

    void onRoomMessage(String body, String from, MinionsRoom muc) {
        minions.onRoomMessage(body, from, muc);
    }
}