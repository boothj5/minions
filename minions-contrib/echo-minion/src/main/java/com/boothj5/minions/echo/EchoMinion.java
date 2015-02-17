package com.boothj5.minions.echo;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;

public class EchoMinion extends Minion {
    private static final String COMMAND = "echo";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " [message] - Echo something.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String trimmed = message.trim();
        if ("".equals(trimmed)) {
            muc.sendMessage(from + " didn't say anything for me to echo");
        } else {
            muc.sendMessage(from + " said: " + trimmed);
        }
    }
}
