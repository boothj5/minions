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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

class MessageHandlerFactory {
    private final MinionStore minions;
    private final String minionsPrefix;
    private final MinionsRoom muc;
    private final String myNick;

    MessageHandlerFactory(MinionStore minions, String minionsPrefix, MultiUserChat muc, String myNick) {
        this.minions = minions;
        this.minionsPrefix = minionsPrefix;
        this.muc = new MinionsRoomImpl(muc);
        this.myNick = myNick;
    }

    MessageHandler create(Message stanza) {
        if (stanza.getBody() != null) {
            if (botCommand(stanza)) {
                return new BotCommandHandler(stanza, minions, minionsPrefix, muc);
            } else if (minionsCommand(stanza)) {
                return new MinionCommandHandler(stanza, minions, minionsPrefix, muc);
            } else {
                return new DefaultMessageHandler(stanza, minions, minionsPrefix, muc);
            }
        } else {
            return new DefaultMessageHandler(stanza, minions, minionsPrefix, muc);
        }
    }

    private boolean botCommand(Message messageStanza) {
        boolean delayed = messageStanza.toXML().contains("delay");
        boolean fromMe = messageStanza.getFrom().endsWith(myNick);
        String help = minionsPrefix + "help";
        String jars = minionsPrefix + "jars";
        boolean isCommand = help.equals(messageStanza.getBody()) || jars.equals(messageStanza.getBody());

        return !delayed && !fromMe && isCommand;
    }

    private boolean minionsCommand(Message messageStanza) {
        boolean delayed = messageStanza.toXML().contains("delay");
        boolean fromMe = messageStanza.getFrom().endsWith(myNick);
        boolean isCommand = messageStanza.getBody().startsWith(minionsPrefix);

        return !delayed && !fromMe && isCommand;
    }
}
