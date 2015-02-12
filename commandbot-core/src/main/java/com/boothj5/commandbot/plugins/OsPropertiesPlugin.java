package com.boothj5.commandbot.plugins;

import com.boothj5.commandbot.CommandBotPlugin;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Enumeration;
import java.util.Properties;

public class OsPropertiesPlugin implements CommandBotPlugin {

    private static final String PROPS = "props";

    @Override
    public String getCommand() {
        return PROPS;
    }

    @Override
    public String getHelp() {
        return "Show OS system properties.";
    }

    @Override
    public void onMessage(MultiUserChat muc, String from, String message) throws XMPPException {
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
