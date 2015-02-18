package com.boothj5.minions.binary;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.lang3.StringUtils;

public class BinaryMinion extends Minion {

    @Override
    public String getHelp() {
        return "to|from [value] - Convert integer to binary, or binary to integer.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            String[] split = StringUtils.split(message, " ");
            String command = split[0];
            String value = split[1];
            switch (command) {
                case "to": {
                    Integer integer = Integer.valueOf(value);
                    String result = Integer.toBinaryString(integer);
                    muc.sendMessage(from + ": " + result);
                    break;
                }
                case "from": {
                    int result = Integer.parseInt(value, 2);
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
