package com.boothj5.minions;

import com.boothj5.minions.api.MinionsException;
import com.boothj5.minions.api.MinionsRoom;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class MinionsRoomImpl implements MinionsRoom {
    private final MultiUserChat muc;

    public MinionsRoomImpl(MultiUserChat muc) {
        this.muc = muc;
    }

    @Override
    public void sendMessage(String message) throws MinionsException {
        try {
            muc.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
            throw new MinionsException("Error sending message:" + message);
        }
    }
}
