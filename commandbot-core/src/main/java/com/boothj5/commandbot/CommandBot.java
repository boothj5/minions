package com.boothj5.commandbot;

import com.boothj5.commandbot.api.CommandBotException;
import com.boothj5.commandbot.api.CommandBotPlugin;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

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
    private final String pluginsDir;

    public CommandBot(String user,
                      String service,
                      String password,
                      String resource,
                      int port,
                      String server,
                      String room,
                      String roomNickname,
                      String roomPassword,
                      String commandPrefix,
                      String pluginsDir) {
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
        this.pluginsDir = pluginsDir;
    }

    public void run() throws CommandBotException {
        try {
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

            Object lock = new Object();
            synchronized (lock) {
                while (true) {
                    lock.wait();
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new CommandBotException("Error");
        }
    }

    private void registerPlugins(PluginStore plugins)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        File pluginsDirFile = new File(pluginsDir);
        File[] files = pluginsDirFile.listFiles();
        URL[] urls = new URL[files.length];
        List<String> pluginClasses = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
            InputStream in = new FileInputStream(files[i]);
            JarInputStream stream = new JarInputStream(in);
            Manifest manifest = stream.getManifest();
            pluginClasses.add(manifest.getMainAttributes().getValue("PluginClass"));
        }

        URLClassLoader loader = new URLClassLoader(urls);

        for (String pluginClass : pluginClasses) {
            Class<?> clazz = Class.forName(pluginClass, true, loader);
            Class<? extends CommandBotPlugin> pluginClazz = clazz.asSubclass(CommandBotPlugin.class);
            Constructor<? extends CommandBotPlugin> ctor = pluginClazz.getConstructor();
            CommandBotPlugin plugin = ctor.newInstance();

            plugins.register(plugin);
        }
    }
}
