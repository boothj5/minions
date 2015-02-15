package com.boothj5.minions;

import com.boothj5.minions.api.MinionsException;
import org.apache.commons.lang3.StringUtils;

public class MinionsMain {
    public static void main(String[] args) throws MinionsException {
        final String service = System.getProperty("minions.user.service");
        final String user = System.getProperty("minions.user.name");
        final String password = System.getProperty("minions.user.password");
        final String resource = System.getProperty("minions.user.resou0rce");

        int port = 5222;
        final String portProperty = System.getProperty("minions.service.port");
        if (!StringUtils.isBlank(portProperty)) {
            port = Integer.valueOf(portProperty);
        }
        final String server = System.getProperty("minions.service.server");

        final String room = System.getProperty("minions.room.jid");
        final String roomNickname = System.getProperty("minions.room.nick");
        final String roomPassword = System.getProperty("minions.room.password");

        String minionsPrefix = "!";
        final String minionsPrefixProperty = System.getProperty("minions.prefix");
        if (!StringUtils.isBlank(minionsPrefixProperty)) {
            minionsPrefix = minionsPrefixProperty;
        }

        String minionsDir = System.getProperty("user.home") + "/.local/share/minions/plugins";
        final String minionsDirProperty = System.getProperty("minions.pluginsdir");
        if (!StringUtils.isBlank(minionsDirProperty)) {
            minionsDir = minionsDirProperty;
        }

        final MinionsRunner minions = new MinoionsBuilder()
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
                .build();

        minions.run();
    }
}
