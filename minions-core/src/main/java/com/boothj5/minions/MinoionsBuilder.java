package com.boothj5.minions;

public class MinoionsBuilder {
    private String service;
    private String user;
    private String password;
    private String resource;
    private int port = 5222;
    private String server;
    private String room;
    private String roomNickname;
    private String roomPassword;
    private String minionsPrefix;
    private String minionsDir;

    public MinoionsBuilder withUser(String user) {
        this.user = user;
        return this;
    }

    public MinoionsBuilder withService(String service) {
        this.service = service;
        return this;
    }

    public MinoionsBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public MinoionsBuilder withResource(String resource) {
        this.resource = resource;
        return this;
    }

    public MinoionsBuilder withServer(String server) {
        this.server = server;
        return this;
    }

    public MinoionsBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public MinoionsBuilder withRoom(String room) {
        this.room = room;
        return this;
    }

    public MinoionsBuilder withRoomNickname(String roomNickname) {
        this.roomNickname = roomNickname;
        return this;
    }

    public MinoionsBuilder withRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
        return this;
    }

    public MinoionsBuilder withMinionsPrefix(String minionsPrefix) {
        this.minionsPrefix = minionsPrefix;
        return this;
    }

    public MinoionsBuilder withMinionsDir(String minionsDir) {
        this.minionsDir = minionsDir;
        return this;
    }

    public MinionsRunner build() {
        return new MinionsRunner(user, service, password, resource, port, server, room, roomNickname, roomPassword, minionsPrefix, minionsDir);
    }
}
