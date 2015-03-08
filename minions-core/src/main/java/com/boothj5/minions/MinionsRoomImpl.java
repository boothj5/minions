package com.boothj5.minions;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

class MinionsRoomImpl implements MinionsRoom {
    private final MultiUserChat muc;

    MinionsRoomImpl(MultiUserChat muc) {
        this.muc = muc;
    }

    @Override
    public void sendMessage(String message) throws MinionsException {
        try {
            muc.sendMessage(message);
        } catch (XMPPException e) {
            throw new MinionsException(e);
        }
    }
}
