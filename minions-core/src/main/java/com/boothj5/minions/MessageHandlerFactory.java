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
        if (validBotCommand(stanza)) {
            return new BotCommandHandler(stanza, minions, minionsPrefix, muc);
        } else if (validMinionsCommand(stanza)) {
            return new MinionCommandHandler(stanza, minions, minionsPrefix, muc);
        } else {
            return new DefaultMessageHandler(stanza, minions, minionsPrefix, muc);
        }
    }

    private boolean validBotCommand(Message messageStanza) {
        boolean containsBody = messageStanza.getBody() != null;
        boolean delayed = messageStanza.toXML().contains("delay");
        boolean fromMe = messageStanza.getFrom().endsWith(myNick);
        boolean isCommand = messageStanza.getBody().startsWith(myNick + ":");
        int length = messageStanza.getBody().length();

        if (containsBody) {
            return !delayed && !fromMe && isCommand && length > myNick.length() + 1;
        } else {
            return false;
        }
    }

    private boolean validMinionsCommand(Message messageStanza) {
        boolean containsBody = messageStanza.getBody() != null;
        boolean delayed = messageStanza.toXML().contains("delay");
        boolean fromMe = messageStanza.getFrom().endsWith(myNick);
        boolean isCommand = messageStanza.getBody().startsWith(minionsPrefix);

        if (containsBody) {
            return !delayed && !fromMe && isCommand;
        } else {
            return false;
        }
    }
}
