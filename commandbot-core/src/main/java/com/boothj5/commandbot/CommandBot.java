package com.boothj5.commandbot;

import com.boothj5.commandbot.plugins.EchoPlugin;
import com.boothj5.commandbot.plugins.HttpStatusPlugin;
import com.boothj5.commandbot.plugins.OsPropertiesPlugin;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class CommandBot {
    private static final Logger LOG = LoggerFactory.getLogger(CommandBot.class);

    private static final int SLEEP_MINUTES = 120;
    public static final String BAREJID = "commandbot";
    public static final String PASSWORD = "password";
    public static final String RESOURCE = "daemon";
    public static final String SERVER = "localhost";
    public static final String SERVICE = "ejabberd.local";
    public static final String ROOMJID = "botroom@conference.ejabberd.local";
    public static final String ROOM_NICK = "commandbot";
    public static final int PORT = 5242;

    public void run() throws XMPPException, InterruptedException {
        PluginStore plugins = new PluginStore();
        registerPlugins(plugins);

        LOG.debug("Starting CommandBot");
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(SERVER, PORT, SERVICE);
        XMPPConnection conn = new XMPPConnection(connectionConfiguration);
        conn.connect();
        conn.login(BAREJID, PASSWORD, RESOURCE);
        LOG.debug(format("Logged in: %s@%s", BAREJID, SERVICE));

        MultiUserChat muc = new MultiUserChat(conn, ROOMJID);
        muc.join(ROOM_NICK);
        LOG.debug(format("Joined: %s as %s", ROOMJID, ROOM_NICK));

        muc.sendMessage("Here are my commands..." + plugins.getHelp());

        BotListener listener = new BotListener(plugins, muc, ROOM_NICK);
        muc.addMessageListener(listener);

        Thread.sleep(60 * 1000 * SLEEP_MINUTES);
    }

    private void registerPlugins(PluginStore plugins) {
        plugins.register(new EchoPlugin());
        plugins.register(new OsPropertiesPlugin());
        plugins.register(new HttpStatusPlugin());
    }
}
