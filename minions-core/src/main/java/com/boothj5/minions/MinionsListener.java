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

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

class MinionsListener implements PacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsListener.class);

    private final MinionStore minions;
    private final MinionsRoom room;
    private final MinionsConfiguration config;

    MinionsListener(MinionsConfiguration config, MinionStore minions, MinionsRoom room) {
        this.config = config;
        this.minions = minions;
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

        if (stanza.toXML().contains("delay")) {
            return;
        }

        JabberID fromJid = new JabberID(stanza.getFrom());
        Optional<String> resource = fromJid.getResource();
        if (!resource.isPresent()) {
            return;
        }

        String occupantNick = resource.get();
        if (occupantNick.equals(config.getMinionsNick())) {
            return;
        }

        if (!body.startsWith(config.getPrefix())) {
            minions.onRoomMessage(body, occupantNick, room);
            return;
        }

        String botCommand = body.substring(config.getPrefix().length());
        if (botCommand.equals(MinionsConfiguration.CMD_HELP)) {
            LOG.debug("Handling help.");
            sendHelp();
            return;
        }
        if (botCommand.equals(MinionsConfiguration.CMD_JARS)) {
            LOG.debug("Handling jars.");
            sendJars();
            return;
        }

        try {
            String minionsCommand = parseMinionsCommand(body);
            minions.lock();
            Minion minion = minions.get(minionsCommand);
            if (minion != null) {
                LOG.debug(format("Handling command: %s", minionsCommand));
                String subMessage;
                int argsIndex = config.getPrefix().length() + minionsCommand.length() + 1;
                subMessage = argsIndex < body.length() ? body.substring(argsIndex) : "";
                minion.onCommandWrapper(room, occupantNick, subMessage);
            } else {
                LOG.debug(format("Minion does not exist: %s", minionsCommand));
                room.sendMessage("No such minion: " + minionsCommand);
            }
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }
    }

    private void sendHelp() {
        try {
            minions.lock();
            List<String> commands = minions.commandList();
            StringBuilder builder = new StringBuilder();
            for (String command : commands) {
                builder.append("\n");
                builder.append(config.getPrefix());
                builder.append(command);
                builder.append(" ");
                builder.append(minions.get(command).getHelp());
            }
            room.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }
    }

    private void sendJars() {
        try {
            minions.lock();
            List<MinionJar> jars = minions.getJars();
            StringBuilder builder = new StringBuilder();
            for (MinionJar jar : jars) {
                builder.append("\n");
                builder.append(jar.getName());
                builder.append(", last updated: ");
                Date date = new Date(jar.getTimestamp());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String format = simpleDateFormat.format(date);
                builder.append(format);
            }
            room.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException e) {
            e.printStackTrace();
        }
    }

    private String parseMinionsCommand(String message) {
        String[] tokens = StringUtils.split(message, " ");
        return tokens[0].substring(config.getPrefix().length());
    }

}
