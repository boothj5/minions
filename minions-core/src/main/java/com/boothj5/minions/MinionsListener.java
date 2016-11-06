/*
 * Copyright 2015 - 2016 James Booth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boothj5.minions;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class MinionsListener implements PacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsListener.class);

    private final MinionStore store;
    private final MinionsRoom room;
    private final MinionsConfiguration config;

    MinionsListener(MinionsConfiguration config, MinionStore store, MinionsRoom room) {
        this.config = config;
        this.store = store;
        this.room = room;
    }

    @Override
    public void processPacket(Packet packet) {
        if (!(packet instanceof Message)) {
            return;
        }

        Message stanza = (Message)packet;
        String body = stanza.getBody();
        if (body == null) {
            return;
        }

        if (stanza.getExtension("delay", "urn:xmpp:delay") != null) {
            return;
        }

        JabberID fromJid = new JabberID(stanza.getFrom());
        Optional<String> resource = fromJid.getResource();
        if (!resource.isPresent()) {
            return;
        }

        String from = resource.get();
        if (from.equals(room.getNick())) {
            return;
        }

        if (!body.startsWith(config.getPrefix())) {
            store.onMessage(from, body);
            return;
        }

        String botCommand = body.substring(config.getPrefix().length());
        if (botCommand.equals(MinionsConfiguration.CMD_HELP)) {
            LOG.debug("Handling help.");
            store.onHelp();
            return;
        }
        if (botCommand.equals(MinionsConfiguration.CMD_JARS)) {
            LOG.debug("Handling jars.");
            store.onJars();
            return;
        }

        store.onCommand(from, body);
    }
}
