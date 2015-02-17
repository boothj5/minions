package com.boothj5.minions;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class MinionsRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsRunner.class);

    private final String user;
    private final String service;
    private final String password;
    private final String resource;
    private final int port;
    private final String server;
    private final String room;
    private final String roomNickname;
    private final String roomPassword;
    private final String minionsPrefix;
    private final String minionsDir;
    private final int refreshSeconds;

    public MinionsRunner(String user, String service, String password, String resource, int port, String server,
                         String room, String roomNickname, String roomPassword, String minionsPrefix, String minionsDir,
                         int refreshSeconds) {
        this.user = user;
        this.service = service;
        this.password = password;
        this.resource = resource;
        this.port = port;
        this.server = server;
        this.room = room;
        this.roomNickname = roomNickname;
        this.roomPassword = roomPassword;
        this.minionsPrefix = minionsPrefix;
        this.minionsDir = minionsDir;
        this.refreshSeconds = refreshSeconds;
    }

    public void run() throws MinionsException {
        try {
            MinionStore minions = new MinionStore(minionsDir, refreshSeconds);

            LOG.debug("Starting MinionsRunner");
            ConnectionConfiguration connectionConfiguration;
            if (StringUtils.isNotBlank(server)) {
                connectionConfiguration = new ConnectionConfiguration(server, port, service);
            } else {
                connectionConfiguration = new ConnectionConfiguration(service, port);
            }

            XMPPConnection conn = new XMPPConnection(connectionConfiguration);
            conn.connect();
            conn.login(user, password, resource);
            LOG.debug(format("Logged in: %s@%s", user, service));

            MultiUserChat muc = new MultiUserChat(conn, room);
            if (StringUtils.isBlank(roomPassword)) {
                muc.join(roomNickname);
            } else {
                muc.join(roomNickname, roomPassword);
            }

            LOG.debug(format("Joined: %s as %s", room, room));

            MinionsListener listener = new MinionsListener(minions, minionsPrefix, muc, roomNickname);
            muc.addMessageListener(listener);

            Object lock = new Object();
            synchronized (lock) {
                while (true) {
                    lock.wait();
                }
            }
        } catch (Throwable t) {
            throw new MinionsException(t);
        }
    }
}
