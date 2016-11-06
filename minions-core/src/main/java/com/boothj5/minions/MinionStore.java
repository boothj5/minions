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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

class MinionStore {
    private static final Logger LOG = LoggerFactory.getLogger(MinionStore.class);
    private final MinionsConfiguration config;
    private final MinionsRoom room;
    private final MinionsMap minionsMap;
    private final MinionsDir dir;
    private final Map<String, MinionJar> currentJars;
    private boolean loaded;

    private boolean isLocked = false;

    MinionStore(MinionsDir dir, MinionsConfiguration config, MinionsRoom room) {
        this.config = config;
        this.dir = dir;
            this.room = room;
        this.minionsMap = new MinionsMap();
        this.currentJars = new HashMap<>();
        this.loaded = false;

        load(room);

        if (config.getRefreshSeconds() > 0) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(() -> load(room),
                config.getRefreshSeconds(),
                config.getRefreshSeconds(),
                TimeUnit.SECONDS);
        }
    }

    void onMessage(String from, String body) {
        try {
            lock();
            LOG.debug(format("%s: Handling message - %s", room.getRoom(), body));
            minionsMap.values().forEach(
                minion -> minion.onMessageWrapper(from, body));
            unlock();
        } catch (Throwable t) {
            LOG.error(format("%s: Error handling message - %s", room.getRoom(), body), t);
        }
    }

    void onCommand(String from, String body) {
        try {
            String[] tokens = StringUtils.split(body, " ");
            String minionsCommand = tokens[0].substring(config.getPrefix().length());
            lock();
            Minion minion = minionsMap.get(minionsCommand);
            if (minion != null) {
                LOG.debug(format("%s: Handling command - %s", room.getRoom(), minionsCommand));
                String subMessage;
                int argsIndex = config.getPrefix().length() + minionsCommand.length() + 1;
                subMessage = argsIndex < body.length() ? body.substring(argsIndex) : "";
                minion.onCommandWrapper(from, subMessage);
            } else {
                LOG.debug(format("%s: Minion does not exist - %s", room.getRoom(), minionsCommand));
                room.sendMessage("No such minion: " + minionsCommand);
            }
            unlock();
        } catch (Throwable t) {
            LOG.error(format("%s: Error handling command - %s", room.getRoom(), body), t);
        }
    }

    void onHelp() {
        try {
            lock();
            StringBuilder builder = new StringBuilder();
            minionsMap.entrySet().forEach(minion -> builder
                .append("\n")
                .append(config.getPrefix())
                .append(minion.getKey())
                .append(" ")
                .append(minion.getValue().getHelp()));
            room.sendMessage(builder.toString());
            unlock();
        } catch (Throwable t) {
            LOG.error(format("%s: Error getting help", room.getRoom()), t);
        }
    }

    void onJars() {
        try {
            lock();
            StringBuilder builder = new StringBuilder();
            currentJars.values().forEach(jar -> builder
                .append("\n")
                .append(jar.getName())
                .append(", last updated: ")
                .append(jar.getTimestampFormat()));
            room.sendMessage(builder.toString());
            unlock();
        } catch (Throwable t) {
            LOG.error(format("%s: Error getting jars", room.getRoom()), t);
        }
    }

    private void load(MinionsRoom room) {
        try {
            lock();
            List<URL> urlList = new ArrayList<>();
            Map<String, MinionJar> newJars = new HashMap<>();
            Map<String, MinionJar> jarsToLoad = new HashMap<>();
            List<String> jarsToRemove = new ArrayList<>();

            dir.listMinionJars().forEach(jar -> {
                newJars.put(jar.getName(), jar);
                if (currentJars.containsKey(jar.getName())) {
                    MinionJar currentMinionJar = currentJars.get(jar.getName());
                    if (!currentMinionJar.getTimestamp().equals(jar.getTimestamp())) {
                        jarsToLoad.put(jar.getName(), jar);
                        urlList.add(jar.getURL());
                        LOG.debug(format("%s: Updated %s", room.getRoom(), jar.getName()));
                        if (loaded) {
                            room.sendMessage("Updated JAR: " + jar.getName());
                        }
                    }
                } else {
                    jarsToLoad.put(jar.getName(), jar);
                    urlList.add(jar.getURL());
                    LOG.debug(format("%s: Added %s", room.getRoom(), jar.getName()));
                    if (loaded) {
                        room.sendMessage("Added JAR: " + jar.getName());
                    }
                }
            });

            currentJars.entrySet().stream()
                .filter(jar -> !newJars.containsKey(jar.getKey()))
                .forEach(jar -> {
                    minionsMap.remove(jar.getValue().getCommand());
                    jarsToRemove.add(jar.getKey());
                    LOG.debug(format("%s: Removed %s", room.getRoom(), jar.getKey()));
                    if (loaded) {
                        room.sendMessage("Removed JAR: " + jar.getKey());
                    }
                });

            URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]));

            jarsToLoad.entrySet().forEach(jar -> {
                MinionJar minionJar = jarsToLoad.get(jar.getKey());
                minionsMap.put(minionJar.getCommand(), minionJar.loadMinionClass(loader, room));
                currentJars.put(jar.getKey(), jar.getValue());
            });

            jarsToRemove.forEach(currentJars::remove);

            loaded = true;
            unlock();
        } catch (Throwable t) {
            LOG.error(format("%s: Error loading minions.", room.getRoom()), t);
        }
    }

    private synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    }

    private synchronized void unlock() {
        isLocked = false;
        notify();
    }
}