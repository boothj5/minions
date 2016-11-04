package com.boothj5.minions.apples;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

public class ApplesMinion extends Minion {
    private int apples = 0;

    public ApplesMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "give|take - Give or take an apple from the minion.";
    }

    @Override
    public void onCommand(String from, String message) {
        switch (message) {
            case "give":
                apples++;
                if (apples == 1) {
                    room.sendMessage(from + ": Thanks, now I have 1 apple :)");
                } else {
                    room.sendMessage(from + ": Thanks, now I have " + apples + " apples :)");
                }
                break;
            case "take":
                if (apples == 0) {
                    room.sendMessage(from + ": Sorry I haven't got any apples.");
                } else {
                    apples--;
                    if (apples == 0) {
                        room.sendMessage(from + ": You took my last apple :(");
                    } else if (apples == 1) {
                        room.sendMessage(from + ": Hmm, now I've only got one left.");
                    } else {
                        room.sendMessage(from + ": Here you go, now I've got " + apples + " apples left.");
                    }
                }
                break;
            case "":
                if (apples == 1) {
                    room.sendMessage(from + ": 1 apple.");
                } else {
                    room.sendMessage(from + ": " + apples + " apples.");
                }
                break;
            default:
                room.sendMessage(from + ": give or take?");
                break;
        }
    }
}
