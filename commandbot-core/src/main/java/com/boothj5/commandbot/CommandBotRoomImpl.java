package com.boothj5.commandbot;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class CommandBotRoomImpl implements CommandBotRoom{
    private final MultiUserChat muc;

    public CommandBotRoomImpl(MultiUserChat muc) {
        this.muc = muc;
    }

    @Override
    public void sendMessage(String message) throws CommandBotException {
        try {
            muc.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
            throw new CommandBotException("Error sending message:" + message);
        }
    }
}
