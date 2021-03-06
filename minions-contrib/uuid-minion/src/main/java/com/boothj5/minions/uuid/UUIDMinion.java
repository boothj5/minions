package com.boothj5.minions.uuid;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

import java.util.UUID;

public class UUIDMinion extends Minion {

    public UUIDMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "- Generate a UUID.";
    }

    @Override
    public void onCommand(String from, String message) {
        UUID uuid = UUID.randomUUID();
        room.sendMessage(from + ": " + uuid.toString());
    }
}
