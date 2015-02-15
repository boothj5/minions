package com.boothj5.commandbot.plugins.osproperties;

import com.boothj5.commandbot.api.CommandBotException;
import com.boothj5.commandbot.api.CommandBotPlugin;
import com.boothj5.commandbot.api.CommandBotRoom;

import java.util.Enumeration;
import java.util.Properties;

public class OsPropertiesPlugin implements CommandBotPlugin {
    private static final String COMMAND = "props";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " - Show OS system properties.";
    }

    @Override
    public void onMessage(CommandBotRoom muc, String from, String message) throws CommandBotException {
        StringBuilder result = new StringBuilder();
        Properties properties = System.getProperties();
        Enumeration keys = properties.keys();

        result.append("\n");
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (key.startsWith("os.")) {
                String value = (String)properties.get(key);
                result.append(key).append(": ").append(value).append("\n");
            }
        }
        muc.sendMessage(result.toString());
    }
}