package com.boothj5.minions.digest;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class DigestMinion extends Minion {
    private final String COMMAND = "digest";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " - Calculate various digests of a given value. Send 'help' for more information.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String[] tokens = StringUtils.split(message, " ");
        String result;
        switch (tokens[1]) {
            case "help":
                String help =
                    "\n" + "sha1 value - Compute sha1 hash of value, returning the result as a hex string." +
                    "\n" + "sha1base64 value - Compute sha1 hash and base64 encode the result.";

                muc.sendMessage(help);
                break;
            case "sha1":
                result = DigestUtils.shaHex(message.substring(13).getBytes(StandardCharsets.UTF_8));
                muc.sendMessage(from + ": " + result);
                break;
            case "sha1base64":
                result = new String(Base64.encodeBase64(DigestUtils.sha(message.substring(19).getBytes(StandardCharsets.UTF_8))));
                muc.sendMessage(from + ": " + result);
                break;
        }
    }
}
