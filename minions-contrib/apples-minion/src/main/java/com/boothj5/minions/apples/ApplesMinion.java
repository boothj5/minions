package com.boothj5.minions.apples;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;

public class ApplesMinion extends Minion {
    public static final String MINIONS_APPLES_PROPERTY = "minions.apples";

    @Override
    public String getHelp() {
        return "give|take - Give or take an apple from the minion.";
    }

    @Override
    public void onRemove() {
        System.clearProperty(MINIONS_APPLES_PROPERTY);
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String currentStr = System.getProperty(MINIONS_APPLES_PROPERTY);
        int total = currentStr == null ? 0 : Integer.valueOf(currentStr);

        switch (message) {
            case "give":
                total++;
                if (total == 1) {
                    muc.sendMessage(from + ": Thanks, now I have 1 apple :)");
                } else {
                    muc.sendMessage(from + ": Thanks, now I have " + total + " apples :)");
                }
                System.setProperty(MINIONS_APPLES_PROPERTY, String.valueOf(total));
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

        System.setProperty(MINIONS_APPLES_PROPERTY, String.valueOf(total));
    }
}
