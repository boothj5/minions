package com.boothj5.minions.digest;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class DigestMinion extends Minion {
    public static final String HELP = "help";
    public static final String SHA1 = "sha1";
    public static final String SHA1BASE64 = "sha1base64";

    @Override
    public String getHelp() {
        return "- Calculate various digests of a given value. Send 'help' for more information.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String[] tokens = StringUtils.split(message, " ");
        String command = tokens[0];
        String result;
        switch (command) {
            case HELP:
                String help =
                    "\n" + "sha1 value - Compute sha1 hash of value, returning the result as a hex string." +
                    "\n" + "sha1base64 value - Compute sha1 hash and base64 encode the result.";

                muc.sendMessage(help);
                break;
            case SHA1:
                int beginIndex = SHA1.length() + 1;
                String substring = message.substring(beginIndex);
                byte[] bytes = substring.getBytes(StandardCharsets.UTF_8);
                result = DigestUtils.shaHex(bytes);
                muc.sendMessage(from + ": " + result);
                break;
            case SHA1BASE64:
                int beginIndex1 = SHA1BASE64.length() + 1;
                String substring1 = message.substring(beginIndex1);
                byte[] bytes1 = substring1.getBytes(StandardCharsets.UTF_8);
                byte[] sha = DigestUtils.sha(bytes1);
                byte[] bytes2 = Base64.encodeBase64(sha);
                result = new String(bytes2);
                muc.sendMessage(from + ": " + result);
                break;
        }
    }
}
