package com.boothj5.minions;

import java.util.Map;

public class MinionsConfiguration {

    public static final String DEFAULT_RESOURCE = "minions-core";
    public static final int DEFAULT_PORT = 5222;
    public static final String DEFAULT_ROOM_NICK = "minions";
    public static final String DEFAULT_PLUGINS_DIR = System.getProperty("user.home") + "/.local/share/minions/plugins";
    public static final int DEFAULT_REFRESH_SECONDS = 10;
    public static final String DEFAULT_PREFIX = "!";
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

    public MinionsConfiguration(Map<String, Map<String, Object>> config) throws MinionsException {
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

    public String getUser() {
        return userName;
    }

    public String getService() {
        return userService;
    }

    public String getResource() {
        return userResource;
    }

    public String getPassword() {
        return userPassword;
    }

    public String getServer() {
        return serviceServer;
    }

    public int getPort() {
        return servicePort;
    }

    public String getRoom() {
        return roomJid;
    }

    public String getRoomNick() {
        return roomNick;
    }

    public int getRefreshSeconds() {
        return pluginsRefreshSeconds;
    }

    public String getPrefix() {
        return pluginsPrefix;
    }

    public String getPluginsDir() {
        return pluginsDir;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    private void validateUser(Map<String, Object> user) throws MinionsException {
        if (user == null) {
            throw new MinionsException("Missing configuration property: user");
        } else {
            if (user.get("name") == null) {
                throw new MinionsException("Missing configuration property: user.name");
            }
            if (user.get("service") == null) {
                throw new MinionsException("Missing configuration property: user.service");
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
        userName = (String) user.get("name");
        userService = (String) user.get("service");
        userPassword = (String) user.get("password");
        if (user.get("resource") != null) {
            userResource = (String) user.get("resource");
        }
    }
}
