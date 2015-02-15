package com.boothj5.commandbot.plugins;

import com.boothj5.commandbot.CommandBotException;
import com.boothj5.commandbot.CommandBotPlugin;
import com.boothj5.commandbot.CommandBotRoom;
import org.apache.commons.lang3.StringUtils;

public class EchoPlugin implements CommandBotPlugin {
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
    public void onMessage(CommandBotRoom muc, String from, String message) throws CommandBotException {
        String[] jid = StringUtils.split(from, "/");
        try {
            String toEcho = message.substring(COMMAND.length() + 2);
            muc.sendMessage(jid[1] + " said: " + toEcho);
        } catch (RuntimeException e) {
            muc.sendMessage(jid[1] + " didn't say anything for me to echo");
        }
    }
}
