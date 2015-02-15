package com.boothj5.minions.echo;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
import com.boothj5.minions.api.MinionsRoom;
import org.apache.commons.lang3.StringUtils;

public class EchoMinion implements Minion {
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
        String[] jid = StringUtils.split(from, "/");
        try {
            String toEcho = message.substring(COMMAND.length() + 2);
            muc.sendMessage(jid[1] + " said: " + toEcho);
        } catch (RuntimeException e) {
            muc.sendMessage(jid[1] + " didn't say anything for me to echo");
        }
    }
}
