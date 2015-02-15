package com.boothj5.commandbot;

public class CommandBotBuilder {
    private String service;
    private String user;
    private String password;
    private String resource;
    private int port = 5222;
    private String server;
    private String room;
    private String roomNickname;
    private String roomPassword;
    private String commandPrefix;
    private String pluginsDir;

    public CommandBotBuilder withUser(String user) {
        this.user = user;
        return this;
    }

    public CommandBotBuilder withService(String service) {
        this.service = service;
        return this;
    }

    public CommandBotBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public CommandBotBuilder withResource(String resource) {
        this.resource = resource;
        return this;
    }

    public CommandBotBuilder withServer(String server) {
        this.server = server;
        return this;
    }

    public CommandBotBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public CommandBotBuilder withRoom(String room) {
        this.room = room;
        return this;
    }

    public CommandBotBuilder withRoomNickname(String roomNickname) {
        this.roomNickname = roomNickname;
        return this;
    }

    public CommandBotBuilder withRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
        return this;
    }

    public CommandBotBuilder withCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
        return this;
    }

    public CommandBotBuilder withPluginsDir(String pluginsDir) {
        this.pluginsDir = pluginsDir;
        return this;
    }

    public CommandBot build() {
        return new CommandBot(user, service, password, resource, port, server, room, roomNickname, roomPassword, commandPrefix, pluginsDir);
    }
}
