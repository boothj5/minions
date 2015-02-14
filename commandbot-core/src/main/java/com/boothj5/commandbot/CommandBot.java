package com.boothj5.commandbot;

import com.boothj5.commandbot.plugins.ChatterBotPlugin;
import com.boothj5.commandbot.plugins.EchoPlugin;
import com.boothj5.commandbot.plugins.HttpStatusPlugin;
import com.boothj5.commandbot.plugins.OsPropertiesPlugin;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class CommandBot {
    private static final Logger LOG = LoggerFactory.getLogger(CommandBot.class);
    private final String user;
    private final String service;
    private final String password;
    private final String resource;
    private final int port;
    private final String server;
    private final String room;
    private final String roomNickname;
    private final String roomPassword;
    private final String commandPrefix;

    public CommandBot(String user,
                      String service,
                      String password,
                      String resource,
                      int port,
                      String server,
                      String room,
                      String roomNickname,
                      String roomPassword,
                      String commandPrefix) {
        this.user = user;
        this.service = service;
        this.password = password;
        this.resource = resource;
        this.port = port;
        this.server = server;
        this.room = room;
        this.roomNickname = roomNickname;
        this.roomPassword = roomPassword;
        this.commandPrefix = commandPrefix;
    }

    public void run() throws XMPPException, InterruptedException {
        PluginStore plugins = new PluginStore();
        registerPlugins(plugins);

        LOG.debug("Starting CommandBot");
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

        BotListener listener = new BotListener(plugins, commandPrefix, muc, roomNickname);
        muc.addMessageListener(listener);

        try {
            Object lock = new Object();
            synchronized (lock) {
                while (true) {
                    lock.wait();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void registerPlugins(PluginStore plugins) {
        plugins.register(new EchoPlugin());
        plugins.register(new OsPropertiesPlugin());
        plugins.register(new HttpStatusPlugin());
        plugins.register(new ChatterBotPlugin());
    }
}
