package com.boothj5.minions;

import com.boothj5.minions.api.MinionsException;
import org.apache.commons.lang3.StringUtils;

public class MinionsMain {
    private static String service;
    private static String user;
    private static String password;
    private static String resource;
    private static int port = 5222;
    private static String server ;
    private static String room;
    private static String roomNickname;
    private static String roomPassword;
    private static String minionsPrefix = "!";
    private static String minionsDir = System.getProperty("user.home") + "/.local/share/minions/plugins";
    private static int refreshSeconds = 10;

    public static void main(String[] args) throws MinionsException {
        readProperties();

        final MinionsRunner minionsRunner = new MinoionsBuilder()
                .withUser(user)
                .withService(service)
                .withPassword(password)
                .withResource(resource)
                .withPort(port)
                .withServer(server)
                .withRoom(room)
                .withRoomNickname(roomNickname)
                .withRoomPassword(roomPassword)
                .withMinionsPrefix(minionsPrefix)
                .withMinionsDir(minionsDir)
                .withRefreshSeconds(refreshSeconds)
                .build();

        minionsRunner.run();
    }

    private static void readProperties() {
        service = System.getProperty("minions.user.service");
        user = System.getProperty("minions.user.name");
        password = System.getProperty("minions.user.password");
        resource = System.getProperty("minions.user.resou0rce");
        server = System.getProperty("minions.service.server");

        final String portProperty = System.getProperty("minions.service.port");
        if (!StringUtils.isBlank(portProperty)) {
            port = Integer.valueOf(portProperty);
        }

        room = System.getProperty("minions.room.jid");
        roomNickname = System.getProperty("minions.room.nick");
        roomPassword = System.getProperty("minions.room.password");

        final String minionsPrefixProperty = System.getProperty("minions.prefix");
        if (!StringUtils.isBlank(minionsPrefixProperty)) {
            minionsPrefix = minionsPrefixProperty;
        }

        final String minionsDirProperty = System.getProperty("minions.pluginsdir");
        if (!StringUtils.isBlank(minionsDirProperty)) {
            minionsDir = minionsDirProperty;
        }

        final String refereshSecondsProperty = System.getProperty("minions.refresh.seconds");
        if (!StringUtils.isBlank(refereshSecondsProperty)) {
            refreshSeconds = Integer.valueOf(refereshSecondsProperty);
        }
    }
}
