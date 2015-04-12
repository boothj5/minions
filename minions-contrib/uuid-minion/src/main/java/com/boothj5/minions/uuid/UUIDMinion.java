package com.boothj5.minions.uuid;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;

import java.util.UUID;

public class UUIDMinion extends Minion {
    @Override
    public String getHelp() {
        return "- Generate a UUID.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        UUID uuid = UUID.randomUUID();
        muc.sendMessage(from + ": " + uuid.toString());
    }
}
