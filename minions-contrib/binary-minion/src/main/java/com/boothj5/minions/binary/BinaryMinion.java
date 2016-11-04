package com.boothj5.minions.binary;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.lang3.StringUtils;

public class BinaryMinion extends Minion {

    public BinaryMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "to|from [value] - Convert integer to binary, or binary to integer.";
    }

    @Override
    public void onCommand(String from, String message) {
        try {
            String[] split = StringUtils.split(message, " ");
            String command = split[0];
            String value = split[1];
            switch (command) {
                case "to": {
                    Integer integer = Integer.valueOf(value);
                    String result = Integer.toBinaryString(integer);
                    room.sendMessage(from + ": " + result);
                    break;
                }
                case "from": {
                    int result = Integer.parseInt(value, 2);
                    room.sendMessage(from + ": " + String.valueOf(result));
                    break;
                }
                default:
                    room.sendMessage(from + ": " + "Invalid command, use 'to [value]' or 'from [value]'");
                    break;
            }
        } catch (RuntimeException re) {
            room.sendMessage(from + ": " + "Invalid command, use 'to [value]' or 'from [value]'");
        }
    }
}
