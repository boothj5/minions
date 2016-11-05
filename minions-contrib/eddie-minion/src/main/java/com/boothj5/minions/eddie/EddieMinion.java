package com.boothj5.minions.eddie;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

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

    @Override
    public void onCommand(String from, String message) {
        Temporal now = LocalDate.now();
        Temporal concert = LocalDate.of(2017, 5, 27);
        long days = ChronoUnit.DAYS.between(now, concert);

        room.sendMessage("Only " + days + " days to go... \\m/");
    }
}
