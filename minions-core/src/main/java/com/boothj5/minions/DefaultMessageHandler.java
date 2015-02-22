package com.boothj5.minions;

import org.jivesoftware.smack.packet.Message;

public class DefaultMessageHandler extends MessageHandler {
    DefaultMessageHandler(Message stanza, MinionStore minions, String minionsPrefix, MinionsRoom muc) {
        super(stanza, minions, minionsPrefix, muc);
    }

    @Override
    public void execute() {

    }
}
