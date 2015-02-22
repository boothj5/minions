package com.boothj5.minions;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinionsListener implements PacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsListener.class);
    private final MessageHandlerFactory messageHandlerFactory;

    public MinionsListener(MinionStore minions, String minionsPrefix, MultiUserChat muc, String myNick) {
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
