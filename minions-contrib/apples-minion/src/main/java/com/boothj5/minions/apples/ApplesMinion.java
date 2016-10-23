package com.boothj5.minions.apples;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;

import java.util.HashMap;
import java.util.Map;

public class ApplesMinion extends Minion {
    private static Map<String, Integer> applesByRoom = new HashMap<>();

    @Override
    public String getHelp() {
        return "give|take - Give or take an apple from the minion.";
    }

    @Override
    public void onCommand(MinionsRoom muc, String from, String message) {
        Integer total = applesByRoom.getOrDefault(muc.getRoom(), 0);

        switch (message) {
            case "give":
                total++;
                if (total == 1) {
                    muc.sendMessage(from + ": Thanks, now I have 1 apple :)");
                } else {
                    muc.sendMessage(from + ": Thanks, now I have " + total + " apples :)");
                }
                applesByRoom.put(muc.getRoom(), total);
                break;
            case "take":
                if (total == 0) {
                    muc.sendMessage(from + ": Sorry I haven't got any apples.");
                } else {
                    total--;
                    if (total == 0) {
                        muc.sendMessage(from + ": You took my last apple :(");
                    } else if (total == 1) {
                        muc.sendMessage(from + ": Hmm, now I've only got one left.");
                    } else {
                        muc.sendMessage(from + ": Here you go, now I've got " + total + " apples left.");
                    }
                    applesByRoom.put(muc.getRoom(), total);
                }
                break;
            case "":
                if (total == 1) {
                    muc.sendMessage(from + ": 1 apple.");
                } else {
                    muc.sendMessage(from + ": " + total + " apples.");
                }
                break;
            default:
                muc.sendMessage(from + ": give or take?");
                break;
        }
    }
}
