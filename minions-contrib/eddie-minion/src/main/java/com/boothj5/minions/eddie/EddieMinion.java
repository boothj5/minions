package com.boothj5.minions.eddie;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

public class EddieMinion extends Minion {

    public EddieMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "- METAL";
    }

    @Override
    public void onMessage(String from, String message) {
        if (message.contains("\\m/")) {
            room.sendMessage("\\m/");
        }

        if (message.contains("/m\\")
                || message.contains("\\m\\")
                || message.contains("/m/")) {
            room.sendMessage(from + ", it's \\m/");

        }

        if (message.toLowerCase().contains("eddie")) {
            room.sendMessage("http://maiden-world.com/images/wallpaper/Iron_Maiden_032_1.jpg");
        }
    }
}
