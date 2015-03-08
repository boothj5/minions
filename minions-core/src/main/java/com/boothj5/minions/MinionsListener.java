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
