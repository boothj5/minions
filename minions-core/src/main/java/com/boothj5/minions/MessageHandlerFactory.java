package com.boothj5.minions;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class MessageHandlerFactory {
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
