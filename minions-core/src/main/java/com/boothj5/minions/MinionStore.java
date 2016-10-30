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
import java.text.SimpleDateFormat;
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

        load();

        if (config.getRefreshSeconds() > 0) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(this::load,
                config.getRefreshSeconds(),
                config.getRefreshSeconds(),
                TimeUnit.SECONDS);
        }
    }

    void onMessage(String body, String from) {
        for (String name : minionsMap.keySet()) {
            Minion minion = minionsMap.get(name);
            minion.onMessageWrapper(room, from, body);
        }
    }

    void onCommand(String body, String occupantNick) {
        try {
            String[] tokens = StringUtils.split(body, " ");
            String minionsCommand = tokens[0].substring(config.getPrefix().length());
            lock();
            Minion minion = minionsMap.get(minionsCommand);
            if (minion != null) {
                LOG.debug(format("Handling command: %s", minionsCommand));
                String subMessage;
                int argsIndex = config.getPrefix().length() + minionsCommand.length() + 1;
                subMessage = argsIndex < body.length() ? body.substring(argsIndex) : "";
                minion.onCommandWrapper(room, occupantNick, subMessage);
            } else {
                LOG.debug(format("Minion does not exist: %s", minionsCommand));
                room.sendMessage("No such minion: " + minionsCommand);
            }
            unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }
    }

    void onHelp() {
        try {
            lock();
            Set<String> commands = minionsMap.keySet();
            StringBuilder builder = new StringBuilder();
            for (String command : commands) {
                builder.append("\n");
                builder.append(config.getPrefix());
                builder.append(command);
                builder.append(" ");
                builder.append(minionsMap.get(command).getHelp());
            }
            room.sendMessage(builder.toString());
            unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }
    }

    void onJars() {
        try {
            lock();
            List<MinionJar> jars = new ArrayList<>();
            jars.addAll(currentJars.values());
            StringBuilder builder = new StringBuilder();
            for (MinionJar jar : jars) {
                builder.append("\n");
                builder.append(jar.getName());
                builder.append(", last updated: ");
                Date date = new Date(jar.getTimestamp());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String format = simpleDateFormat.format(date);
                builder.append(format);
            }
            room.sendMessage(builder.toString());
            unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            lock();
            List<URL> urlList = new ArrayList<>();
            Map<String, MinionJar> newJars = new HashMap<>();
            Map<String, MinionJar> jarsToLoad = new HashMap<>();
            List<String> jarsToRemove = new ArrayList<>();

            List<MinionJar> newMinionJars = dir.listMinionJars();
            for (MinionJar newMinionJar : newMinionJars) {
                newJars.put(newMinionJar.getName(), newMinionJar);
                if (currentJars.containsKey(newMinionJar.getName())) {
                    MinionJar currentMinionJar = currentJars.get(newMinionJar.getName());
                    if (!currentMinionJar.getTimestamp().equals(newMinionJar.getTimestamp())) {
                        jarsToLoad.put(newMinionJar.getName(), newMinionJar);
                        urlList.add(newMinionJar.getURL());
                        LOG.debug("Updated JAR: " + newMinionJar.getName());
                        if (loaded) {
                            room.sendMessage("Updated JAR: " + newMinionJar.getName());
                        }
                    }
                } else {
                    jarsToLoad.put(newMinionJar.getName(), newMinionJar);
                    urlList.add(newMinionJar.getURL());
                    LOG.debug("Added JAR: " + newMinionJar.getName());
                    if (loaded) {
                        room.sendMessage("Added JAR: " + newMinionJar.getName());
                    }
                }
            }

            for (String currentJar : currentJars.keySet()) {
                if (!newJars.containsKey(currentJar)) {
                    minionsMap.remove(currentJars.get(currentJar).getCommand());
                    jarsToRemove.add(currentJar);
                    LOG.debug("Removed JAR: " + currentJar);
                    if (loaded) {
                        room.sendMessage("Removed JAR: " + currentJar);
                    }
                }
            }

            URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]));

            for (String jarToLoad : jarsToLoad.keySet()) {
                MinionJar minionJarToLoad = jarsToLoad.get(jarToLoad);
                minionsMap.put(minionJarToLoad.getCommand(), minionJarToLoad.loadMinionClass(loader));
                currentJars.put(jarToLoad, jarsToLoad.get(jarToLoad));
            }

            jarsToRemove.forEach(currentJars::remove);

            loaded = true;
            unlock();
        } catch (MinionsException | InterruptedException e) {
            LOG.error("Error loading minions.", e);
            e.printStackTrace();
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