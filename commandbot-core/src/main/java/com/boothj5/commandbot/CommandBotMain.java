package com.boothj5.commandbot;

import com.boothj5.commandbot.api.CommandBotException;
import org.apache.commons.lang3.StringUtils;

public class CommandBotMain {
    public static void main(String[] args) throws CommandBotException {
        final String service = System.getProperty("commandbot.user.service");
        final String user = System.getProperty("commandbot.user.name");
        final String password = System.getProperty("commandbot.user.password");
        final String resource = System.getProperty("commandbot.user.resou0rce");

        int port = 5222;
        final String portProperty = System.getProperty("commandbot.service.port");
        if (!StringUtils.isBlank(portProperty)) {
            port = Integer.valueOf(portProperty);
        }
        final String server = System.getProperty("commandbot.service.server");

        final String room = System.getProperty("commandbot.room.jid");
        final String roomNickname = System.getProperty("commandbot.room.nick");
        final String roomPassword = System.getProperty("commandbot.room.password");

        String commandPrefix = "!";
        final String commandPrefixProperty = System.getProperty("commandbot.prefix");
        if (!StringUtils.isBlank(commandPrefixProperty)) {
            commandPrefix = commandPrefixProperty;
        }

        String pluginsDir = System.getProperty("user.home") + "/.local/share/commandbot/plugins";
        final String pluginsDirProperty = System.getProperty("commandbot.pluginsdir");
        if (!StringUtils.isBlank(pluginsDirProperty)) {
            pluginsDir = pluginsDirProperty;
        }

        final CommandBot commandBot = new CommandBotBuilder()
                .withUser(user)
                .withService(service)
                .withPassword(password)
                .withResource(resource)
                .withPort(port)
                .withServer(server)
                .withRoom(room)
                .withRoomNickname(roomNickname)
                .withRoomPassword(roomPassword)
                .withCommandPrefix(commandPrefix)
                .withPluginsDir(pluginsDir)
                .build();

        commandBot.run();
    }
}
