/*
 * Copyright 2015 James Booth
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

import java.util.Map;

class MinionsConfiguration {

    private static final String DEFAULT_RESOURCE = "minions-core";
    private static final int DEFAULT_PORT = 5222;
    private static final String DEFAULT_ROOM_NICK = "minions";
    private static final String DEFAULT_PLUGINS_DIR = System.getProperty("user.home") + "/.local/share/minions/plugins";
    private static final int DEFAULT_REFRESH_SECONDS = 10;
    private static final String DEFAULT_PREFIX = "!";
    private String userService;

    private String userName;
    private String userPassword;
    private String userResource = DEFAULT_RESOURCE;
    private String serviceServer;
    private int servicePort = DEFAULT_PORT;
    private String roomJid;
    private String roomNick = DEFAULT_ROOM_NICK;
    private String roomPassword;
    private String pluginsDir = DEFAULT_PLUGINS_DIR;
    private int pluginsRefreshSeconds = DEFAULT_REFRESH_SECONDS;
    private String pluginsPrefix = DEFAULT_PREFIX;

    MinionsConfiguration(Map<String, Map<String, Object>> config) throws MinionsException {
        Map<String, Object> user = config.get("user");
        Map<String, Object> service = config.get("service");
        Map<String, Object> room = config.get("room");
        Map<String, Object> plugins = config.get("plugins");

        validateUser(user);
        validateRoom(room);

        loadUserConfig(user);
        loadServiceConfig(service);
        loadRoomConfig(room);
        loadPluginsConfig(plugins);
    }

    String getUser() {
        return userName;
    }

    String getService() {
        return userService;
    }

    String getResource() {
        return userResource;
    }

    String getPassword() {
        return userPassword;
    }

    String getServer() {
        return serviceServer;
    }

    int getPort() {
        return servicePort;
    }

    String getRoom() {
        return roomJid;
    }

    String getRoomNick() {
        return roomNick;
    }

    int getRefreshSeconds() {
        return pluginsRefreshSeconds;
    }

    String getPrefix() {
        return pluginsPrefix;
    }

    String getPluginsDir() {
        return pluginsDir;
    }

    String getRoomPassword() {
        return roomPassword;
    }

    private void validateUser(Map<String, Object> user) throws MinionsException {
        if (user == null) {
            throw new MinionsException("Missing configuration property: user");
        } else {
            if (user.get("name") == null) {
                throw new MinionsException("Missing configuration property: user.name");
            } else {
                String jid = (String) user.get("name");
                if (!jid.contains("@")) {
                    throw new MinionsException("Invalid property user.name, specify a valid Jabber ID");
                }
            }
            if (user.get("password") == null) {
                throw new MinionsException("Missing configuration property: user.password");
            }
        }
    }

    private void validateRoom(Map<String, Object> room) throws MinionsException {
        if (room == null) {
            throw new MinionsException("Missing configuration property: room");
        } else {
            if (room.get("jid") == null) {
                throw new MinionsException("Missing configuration property: room.jid");
            }
        }
    }

    private void loadPluginsConfig(Map<String, Object> plugins) {
        if (plugins != null) {
            if (plugins.get("dir") != null) {
                pluginsDir = (String) plugins.get("dir");
            }
            if (plugins.get("refreshSeconds") != null) {
                pluginsRefreshSeconds = (int) plugins.get("refreshSeconds");
            }
            if (plugins.get("prefix") != null) {
                pluginsPrefix = (String) plugins.get("prefix");
            }
        }
    }

    private void loadRoomConfig(Map<String, Object> room) {
        roomJid = (String) room.get("jid");
        if (room.get("nick") != null) {
            roomNick = (String) room.get("nick");
        }
        roomPassword = (String) room.get("password");
    }

    private void loadServiceConfig(Map<String, Object> service) {
        if (service != null) {
            serviceServer = (String) service.get("server");
            if (service.get("port") != null) {
                servicePort = (int) service.get("port");
            }
        }
    }

    private void loadUserConfig(Map<String, Object> user) {
        String jid = (String) user.get("name");
        String[] split = jid.split("@");
        userName = split[0];
        userService = split[1];
        userPassword = (String) user.get("password");
        if (user.get("resource") != null) {
            userResource = (String) user.get("resource");
        }
    }
}
