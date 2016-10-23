package com.boothj5.minions.echo;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

public class EchoMinion extends Minion {

    @Override
    public String getHelp() {
        return "[message] - Echo something.";
    }

    @Override
    public void onCommand(MinionsRoom muc, String from, String message) {
        String trimmed = message.trim();
        if ("".equals(trimmed)) {
            muc.sendMessage(from + " didn't say anything for me to echo");
        } else {
            muc.sendMessage(from + " said: " + trimmed);
        }
    }
}
