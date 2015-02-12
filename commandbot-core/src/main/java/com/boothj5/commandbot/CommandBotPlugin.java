package com.boothj5.commandbot;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

public interface CommandBotPlugin {
    String getCommand();
    String getHelp();
    void onMessage(MultiUserChat muc, String from, String message) throws XMPPException;
}
