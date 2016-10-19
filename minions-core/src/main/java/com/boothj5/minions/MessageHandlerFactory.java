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
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Optional;

class MessageHandlerFactory {
    private final MinionStore minions;
    private final String minionsPrefix;
    private final MinionsRoom muc;
    private final String myNick;
    private final String cmdHelp;
    private final String cmdJars;

    MessageHandlerFactory(MinionStore minions, String minionsPrefix, MultiUserChat muc, String myNick) {
        this.minions = minions;
        this.minionsPrefix = minionsPrefix;
        this.muc = new MinionsRoomImpl(muc);
        this.myNick = myNick;
        this.cmdHelp = minionsPrefix + MinionsConfiguration.HELP;
        this.cmdJars = minionsPrefix + MinionsConfiguration.JARS;
    }

    MessageHandler create(Message stanza) {
        String body = stanza.getBody();

        if (body == null) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc);
        }

        if (stanza.toXML().contains("delay")) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc);
        }

        JabberID fromJid = new JabberID(stanza.getFrom());
        Optional<String> resource = fromJid.getResource();
        if (!resource.isPresent()) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc);
        }

        if (resource.get().equals(myNick)) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc);
        }

        if (cmdHelp.equals(body)) {
            return new BotCommandHandler(stanza, minions, minionsPrefix, muc);
        }

        if (cmdJars.equals(body)) {
            return new BotCommandHandler(stanza, minions, minionsPrefix, muc);
        }

        if (body.startsWith(minionsPrefix)) {
            return new MinionCommandHandler(stanza, minions, minionsPrefix, muc);
        }

        return new RoomMessageHandler(stanza, minions, minionsPrefix, muc);
    }
}
