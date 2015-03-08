package com.boothj5.minions;

import org.jivesoftware.smack.packet.Message;

abstract class MessageHandler {
    final Message stanza;
    final MinionStore minions;
    final String minionsPrefix;
    final MinionsRoom muc;

    MessageHandler(Message stanza, MinionStore minions, String minionsPrefix, MinionsRoom muc) {
        this.stanza = stanza;
        this.minions = minions;
        this.minionsPrefix = minionsPrefix;
        this.muc = muc;
    }

    abstract void execute();
}