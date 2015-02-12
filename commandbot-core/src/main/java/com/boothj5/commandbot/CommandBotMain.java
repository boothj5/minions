package com.boothj5.commandbot;

import com.boothj5.commandbot.plugins.EchoPlugin;
import com.boothj5.commandbot.plugins.OsPropertiesPlugin;
import org.jivesoftware.smack.*;

public class CommandBotMain {
    public static void main(String[] args) throws XMPPException, InterruptedException {
        CommandBot commandBot = new CommandBot();
        commandBot.run();
    }
}
