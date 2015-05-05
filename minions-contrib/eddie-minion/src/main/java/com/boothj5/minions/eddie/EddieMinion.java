package com.boothj5.minions.eddie;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;

public class EddieMinion extends Minion {
    @Override
    public String getHelp() {
        return "- METAL";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        if (message.contains("\\m/")) {
            muc.sendMessage("\\m/");
        } else if (message.contains("/m\\")
                || message.contains("\\m\\")
                || message.contains("/m/")) {
            muc.sendMessage(from + ", it's \\m/");
        } else if (message.toLowerCase().contains("eddie")) {
            muc.sendMessage("http://maiden-world.com/images/wallpaper/Iron_Maiden_032_1.jpg");
        }
    }
}
