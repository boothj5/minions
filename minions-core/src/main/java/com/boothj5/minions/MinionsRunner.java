package com.boothj5.minions;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
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

    public MinionsRunner(String user,
                         String service,
                         String password,
                         String resource,
                         int port,
                         String server,
                         String room,
                         String roomNickname,
                         String roomPassword,
                         String minionsPrefix,
                         String minionsDir) {
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
    }

    public void run() throws MinionsException {
        try {
            MinionStore minions = new MinionStore();
            registerMinions(minions);

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
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new MinionsException("Error");
        }
    }

    private void registerMinions(MinionStore minions)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        File minionsDirFile = new File(minionsDir);
        File[] files = minionsDirFile.listFiles();
        URL[] urls = new URL[files.length];
        List<String> minionClasses = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
            InputStream in = new FileInputStream(files[i]);
            JarInputStream stream = new JarInputStream(in);
            Manifest manifest = stream.getManifest();
            minionClasses.add(manifest.getMainAttributes().getValue("MinionClass"));
        }

        URLClassLoader loader = new URLClassLoader(urls);

        for (String minionClass : minionClasses) {
            Class<?> clazz = Class.forName(minionClass, true, loader);
            Class<? extends Minion> minionClazz = clazz.asSubclass(Minion.class);
            Constructor<? extends Minion> ctor = minionClazz.getConstructor();
            Minion minion = ctor.newInstance();

            minions.register(minion);
        }
    }
}
