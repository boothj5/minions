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

import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class BotCommandHandler extends MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BotCommandHandler.class);

    BotCommandHandler(Message stanza, MinionStore minions, String minionsPrefix, MinionsRoom muc) {
        super(stanza, minions, minionsPrefix, muc);
    }

    @Override
    void execute() {
        String command = stanza.getBody().substring(minionsPrefix.length());
        try {
            switch (command) {
                case MinionsConfiguration.HELP:
                    LOG.debug("Handling help.");
                    handleHelp();
                    break;
                case MinionsConfiguration.JARS:
                    LOG.debug("Handling jars.");
                    handleJars();
                    break;
                default:
                    muc.sendMessage("Unknown command: " + command);
                    break;
            }
        } catch (MinionsException e) {
            e.printStackTrace();
        }
    }

    private void handleJars() {
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
            muc.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException e) {
            e.printStackTrace();
        }

    }

    private void handleHelp() {
        try {
            minions.lock();
            List<String> commands = minions.commandList();
            StringBuilder builder = new StringBuilder();
            for (String command : commands) {
                builder.append("\n");
                builder.append(minionsPrefix);
                builder.append(command);
                builder.append(" ");
                builder.append(minions.get(command).getHelp());
            }
            muc.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }
    }
}