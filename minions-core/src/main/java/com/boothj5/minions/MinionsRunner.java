package com.boothj5.minions;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

class MinionsRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsRunner.class);

    private final MinionsConfiguration config;

    MinionsRunner(MinionsConfiguration config) {
        this.config = config;
    }

    void run() throws MinionsException {
        try {
            LOG.debug("Starting MinionsRunner");
            ConnectionConfiguration connectionConfiguration;
            if (StringUtils.isNotBlank(config.getServer())) {
                connectionConfiguration = new ConnectionConfiguration(config.getServer(), config.getPort(), config.getService());
            } else {
                connectionConfiguration = new ConnectionConfiguration(config.getService(), config.getPort());
            }

            XMPPConnection conn = new XMPPConnection(connectionConfiguration);
            conn.connect();
            conn.login(config.getUser(), config.getPassword(), config.getResource());
            LOG.debug(format("Logged in: %s@%s", config.getUser(), config.getService()));

            MultiUserChat muc = new MultiUserChat(conn, config.getRoom());
            if (StringUtils.isBlank(config.getRoomPassword())) {
                muc.join(config.getRoomNick());
            } else {
                muc.join(config.getRoomNick(), config.getRoomPassword());
            }

            LOG.debug(format("Joined: %s as %s", config.getRoom(), config.getRoomNick()));

            MinionStore minions = new MinionStore(config.getPluginsDir(), config.getRefreshSeconds(), muc);

            MinionsListener listener = new MinionsListener(minions, config.getPrefix(), muc, config.getRoomNick());

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
