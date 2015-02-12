package com.boothj5.commandbot.plugins;

import com.boothj5.commandbot.CommandBotPlugin;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoPlugin implements CommandBotPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(EchoPlugin.class);

    private static final String COMMAND = "echo";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return "Echo something.";
    }

    @Override
    public void onMessage(MultiUserChat muc, String from, String message) throws XMPPException {
        String[] jid = StringUtils.split(from, "/");
        try {
            String toEcho = message.substring(COMMAND.length() + 2);
            muc.sendMessage(jid[1] + " said: " + toEcho);
        } catch (RuntimeException e) {
            muc.sendMessage(jid[1] + " didn't say anything for me to echo");
        }
    }
}
