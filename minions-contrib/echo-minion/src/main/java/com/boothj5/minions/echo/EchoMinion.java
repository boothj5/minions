package com.boothj5.minions.echo;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

public class EchoMinion extends Minion {

    public EchoMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "[message] - Echo something.";
    }

    @Override
    public void onCommand(String from, String message) {
        String trimmed = message.trim();
        if ("".equals(trimmed)) {
            room.sendMessage(from + " didn't say anything for me to echo");
        } else {
            room.sendMessage(from + " said: " + trimmed);
        }
    }
}
