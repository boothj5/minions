package com.boothj5.minions.osproperties;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

import java.util.Enumeration;
import java.util.Properties;

public class OsPropertiesMinion extends Minion {

    public OsPropertiesMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "- Show OS system properties.";
    }

    @Override
    public void onCommand(String from, String message) {
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
        room.sendMessage(result.toString());
    }
}