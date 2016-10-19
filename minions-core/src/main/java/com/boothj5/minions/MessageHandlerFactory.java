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

class MessageHandlerFactory {
    private static final String CMD_HELP = "!help";
    private static final String CMD_JARS = "!jars";

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
        if (stanza.getBody() == null) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc, myNick);
        }

        if (stanza.toXML().contains("delay")) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc, myNick);
        }

        if (stanza.getFrom().endsWith(myNick)) {
            return new NopMessageHandler(stanza, minions, minionsPrefix, muc, myNick);
        }

        if (CMD_HELP.equals(stanza.getBody())) {
            return new BotCommandHandler(stanza, minions, minionsPrefix, muc, myNick);
        }

        if (CMD_JARS.equals(stanza.getBody())) {
            return new BotCommandHandler(stanza, minions, minionsPrefix, muc, myNick);
        }

        if (stanza.getBody().startsWith(minionsPrefix)) {
            return new MinionCommandHandler(stanza, minions, minionsPrefix, muc, myNick);
        }

        return new RoomMessageHandler(stanza, minions, minionsPrefix, muc, myNick);
    }
}
