package com.boothj5.minions.binary;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
import com.boothj5.minions.api.MinionsRoom;
import org.apache.commons.lang3.StringUtils;

public class BinaryMinion implements Minion {
    private final String COMMAND = "bin";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " to|from [value] - Convert integer to binary, or binary to integer.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            String[] split = StringUtils.split(message, " ");
            String command = split[1];
            switch (command) {
                case "to": {
                    Integer integer = Integer.valueOf(split[2]);
                    String result = Integer.toBinaryString(integer);
                    muc.sendMessage(from + ": " + result);
                    break;
                }
                case "from": {
                    int result = Integer.parseInt(split[2], 2);
                    muc.sendMessage(from + ": " + String.valueOf(result));
                    break;
                }
                default:
                    muc.sendMessage(from + ": " + "Invalid command, use 'to [value]' or 'from [value]'");
                    break;
            }
        } catch (RuntimeException re) {
            muc.sendMessage(from + ": " + "Invalid command, use 'to [value]' or 'from [value]'");
        }
    }
}
