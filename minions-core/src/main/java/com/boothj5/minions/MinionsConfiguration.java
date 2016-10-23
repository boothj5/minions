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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MinionsConfiguration {
    static final String CMD_HELP = "help";
    static final String CMD_JARS = "jars";

    private static final String DEFAULT_RESOURCE = "minions-core";
    private static final int DEFAULT_PORT = 5222;
    private static final String DEFAULT_PLUGINS_DIR = System.getProperty("user.home") + "/.local/share/minions/plugins";
    private static final int DEFAULT_REFRESH_SECONDS = 10;
    private static final String DEFAULT_PREFIX = "!";

    private String userName;
    private String userPassword;
    private String userResource = DEFAULT_RESOURCE;
    private String userService;
    private String serviceServer;
    private int servicePort = DEFAULT_PORT;

    private List<MinionsRoomConfiguration> rooms = new ArrayList<>();

    private String pluginsDir = DEFAULT_PLUGINS_DIR;
    private int pluginsRefreshSeconds = DEFAULT_REFRESH_SECONDS;
    private String pluginsPrefix = DEFAULT_PREFIX;

    MinionsConfiguration(Map<String, Object> config) {
        Object user = config.get("user");
        Object service = config.get("service");
        Object rooms = config.get("rooms");
        Object plugins = config.get("plugins");

        validateUser(user);
        validateRooms(rooms);

        loadUserConfig(user);
        loadServiceConfig(service);
        loadRoomsConfig(rooms);
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

    int getRefreshSeconds() {
        return pluginsRefreshSeconds;
    }

    String getPrefix() {
        return pluginsPrefix;
    }

    String getPluginsDir() {
        return pluginsDir;
    }

    List<MinionsRoomConfiguration> getRooms() {
        return rooms;
    }

    private void validateUser(Object user) {
        if (user == null) {
            throw new MinionsException("Missing configuration property: user");
        }

        Map<String, String> userProps = (Map<String, String>)user;

        if (userProps.get("name") == null) {
            throw new MinionsException("Missing configuration property: user.name");
        }

        String jid = userProps.get("name");
        if (!jid.contains("@")) {
            throw new MinionsException("Invalid property user.name, specify a valid Jabber ID");
        }

        if (userProps.get("password") == null) {
            throw new MinionsException("Missing configuration property: user.password");
        }
    }

    private void validateRooms(Object rooms) {
        if (rooms == null) {
            throw new MinionsException("Missing configuration property: rooms");
        }

        List<Map<String, String>> roomsList = (List<Map<String, String>>) rooms;
        if (roomsList.size() == 0) {
            throw new MinionsException("Must have at least one room configured.");
        }
    }

    private void loadPluginsConfig(Object plugins) {
        if (plugins == null) {
            return;
        }

        Map<String, Object> pluginsProps = (Map<String, Object>)plugins;

        if (pluginsProps.get("dir") != null) {
            pluginsDir = (String)pluginsProps.get("dir");
        }
        if (pluginsProps.get("refreshSeconds") != null) {
            pluginsRefreshSeconds = (int)pluginsProps.get("refreshSeconds");
        }
        if (pluginsProps.get("prefix") != null) {
            pluginsPrefix = (String)pluginsProps.get("prefix");
        }
    }

    private void loadRoomsConfig(Object roomsConf) {
        List<Map<String, String>> roomsList = (List<Map<String, String>>)roomsConf;

        for (Map<String, String> room : roomsList) {
            String roomJid = room.get("jid");
            String minionsNick = room.get("nick");
            String roomPassword = room.get("password");

            rooms.add(new MinionsRoomConfiguration(roomJid, minionsNick, roomPassword));
        }
    }

    private void loadServiceConfig(Object service) {
        if (service == null) {
            return;
        }

        Map<String, Object> serviceProps = (Map<String, Object>)service;

        serviceServer = (String)serviceProps.get("server");
        if (serviceProps.get("port") != null) {
            servicePort = (int)serviceProps.get("port");
        }
    }

    private void loadUserConfig(Object user) {
        Map<String, String> userProps = (Map<String, String>)user;
        String jid = userProps.get("name");
        String[] split = jid.split("@");
        userName = split[0];
        userService = split[1];
        userPassword = userProps.get("password");
        if (userProps.get("resource") != null) {
            userResource = userProps.get("resource");
        }
    }
}
