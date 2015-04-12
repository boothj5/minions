/*
 * Copyright 2015 James Booth
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
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

class MinionCommandHandler extends MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MinionCommandHandler.class);

    MinionCommandHandler(Message stanza, MinionStore minions, String minionsPrefix, MinionsRoom muc) {
        super(stanza, minions, minionsPrefix, muc);
    }

    @Override
    void execute() {
        try {
            String command = parseMinionsCommand(stanza.getBody());
            minions.lock();
            Minion minion = minions.get(command);
            if (minion != null) {
                LOG.debug(format("Handling command: %s", command));
                String from = org.jivesoftware.smack.util.StringUtils.parseResource(stanza.getFrom());
                String subMessage;
                try {
                    subMessage = stanza.getBody().substring(minionsPrefix.length() + command.length() + 1);
                } catch (IndexOutOfBoundsException e) {
                    subMessage = "";
                }
                minion.onMessageWrapper(muc, from, subMessage);
            } else {
                LOG.debug(format("Minion does not exist: %s", command));
                muc.sendMessage("No such minion: " + command);
            }
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }

    }

    private String parseMinionsCommand(String message) {
        String[] tokens = StringUtils.split(message, " ");
        return tokens[0].substring(minionsPrefix.length());
    }
}
