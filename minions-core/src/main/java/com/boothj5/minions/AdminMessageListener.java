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

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

class AdminMessageListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMessageListener.class);
    private final MinionsConfiguration config;
    private final Map<String, MinionsRoom> rooms;

    AdminMessageListener(MinionsConfiguration config, Map<String, MinionsRoom> rooms) {
        this.config = config;
        this.rooms = rooms;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        String body = message.getBody();
        if (body == null) {
            LOG.debug("No message body.");
            return;
        }

        LOG.debug("Admin message received - " + message.getFrom() + ": " + body);

        String from = message.getFrom();
        JabberID fromJid = new JabberID(from);
        Optional<String> bareJid = fromJid.getBareJid();

        if (!bareJid.isPresent()) {
            LOG.debug("No barejid found.");
            return;
        }

        if (!config.getAdmins().contains(bareJid.get())) {
            LOG.debug("barejid is not in admins list.");
            return;
        }


        String[] splitBody = body.split(" ", 3);
        switch (splitBody[0]) {
            case "help":    handleHelp(chat, splitBody);    break;
            case "rooms":   handleRooms(chat, splitBody);   break;
            case "send":    handleSend(chat, splitBody);    break;
            case "me":      handleMe(chat, splitBody);      break;
            default:        handleDefault(chat);            break;
        }
    }

    private void handleHelp(Chat chat, String[] tokens) {
        try {
            if (tokens.length != 1) {
                chat.sendMessage("Invalid command usage... duh");
                return;
            }

            String help = "\n" +
                "help - This help\n" +
                "rooms - List rooms I'm currently in\n" +
                "send <room> <message> - Send a message to the specified room\n" +
                "me <room> <message> - Send a /me message to the specified room";
            chat.sendMessage(help);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private void handleRooms(Chat chat, String[] tokens) {
        try {
            if (tokens.length != 1) {
                chat.sendMessage("Invalid command usage... duh");
                return;
            }

            StringBuilder builder = new StringBuilder();
            rooms.values().forEach(room -> builder
                .append("\n")
                .append(room.getRoom())
                .append(" as ")
                .append(room.getNick()));
            chat.sendMessage(builder.toString());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private void handleSend(Chat chat, String[] tokens) {
        try {
            if (tokens.length != 3) {
                chat.sendMessage("Invalid command usage... duh");
                return;
            }

            MinionsRoom room = rooms.get(tokens[1]);
            if (room == null) {
                chat.sendMessage("Room doesn't exist :/");
            } else {
                room.sendMessage(tokens[2]);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private void handleMe(Chat chat, String[] tokens) {
        try {
            if (tokens.length != 3) {
                chat.sendMessage("Invalid command usage... duh");
                return;
            }

            MinionsRoom room = rooms.get(tokens[1]);
            if (room == null) {
                chat.sendMessage("Room doesn't exist :/");
            } else {
                room.sendMessage("/me " + tokens[2]);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private void handleDefault(Chat chat) {
        try {
            chat.sendMessage("I didn't understand that...");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
