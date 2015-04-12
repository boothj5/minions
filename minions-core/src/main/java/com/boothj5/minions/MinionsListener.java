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

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

class MinionsListener implements PacketListener {
    private final MessageHandlerFactory messageHandlerFactory;

    MinionsListener(MinionStore minions, String minionsPrefix, MultiUserChat muc, String myNick) {
        messageHandlerFactory = new MessageHandlerFactory(minions, minionsPrefix, muc, myNick);
    }

    @Override
    public void processPacket(Packet packet) {
        if (packet instanceof Message) {
            MessageHandler handler = messageHandlerFactory.create((Message) packet);
            handler.execute();
        }
    }
}
