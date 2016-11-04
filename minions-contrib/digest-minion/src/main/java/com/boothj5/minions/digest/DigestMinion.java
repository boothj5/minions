package com.boothj5.minions.digest;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class DigestMinion extends Minion {
    private static final String HELP = "help";
    private static final String SHA1 = "sha1";
    private static final String SHA1BASE64 = "sha1base64";
    private static final String SHA256 = "sha256";
    private static final String SHA256BASE64 = "sha256base64";
    private static final String SHA512 = "sha512";
    private static final String SHA512BASE64 = "sha512base64";
    private static final String MD5 = "md5";
    private static final String MD5BASE64 = "md5base64";

    public DigestMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "- Calculate various digests of a given value. Send 'help' for more information.";
    }

    @Override
    public void onCommand(String from, String message) {
        String[] tokens = StringUtils.split(message, " ");
        String command = tokens[0];

        int beginIndex;
        byte[] bytes, hash, base64;
        String result, substring;

        switch (command) {
            case HELP:
                String help =
                    "\n" + "sha1 value - Compute sha1 hash of value, returning the result as a hex string." +
                    "\n" + "sha1base64 value - Compute sha1 hash and base64 encode the result." +
                    "\n" + "sha256 value - Compute sha256 hash of value, returning the result as a hex string." +
                    "\n" + "sha256base64 value - Compute sha256 hash and base64 encode the result." +
                    "\n" + "sha512 value - Compute sha512 hash of value, returning the result as a hex string." +
                    "\n" + "sha512base64 value - Compute sha512 hash and base64 encode the result." +
                    "\n" + "md5 value - Compute md5 hash of value, returning the result as a hex string." +
                    "\n" + "md5base64 value - Compute md5 hash and base64 encode the result.";
                room.sendMessage(help);
                break;
            case SHA1:
                beginIndex = SHA1.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                result = DigestUtils.shaHex(bytes);
                room.sendMessage(from + ": " + result);
                break;
            case SHA1BASE64:
                beginIndex = SHA1BASE64.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                hash = DigestUtils.sha(bytes);
                base64 = Base64.encodeBase64(hash);
                result = new String(base64);
                room.sendMessage(from + ": " + result);
                break;
            case SHA256:
                beginIndex = SHA256.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                result = DigestUtils.sha256Hex(bytes);
                room.sendMessage(from + ": " + result);
                break;
            case SHA256BASE64:
                beginIndex = SHA256BASE64.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                hash = DigestUtils.sha256(bytes);
                base64 = Base64.encodeBase64(hash);
                result = new String(base64);
                room.sendMessage(from + ": " + result);
                break;
            case SHA512:
                beginIndex = SHA512.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                result = DigestUtils.sha512Hex(bytes);
                room.sendMessage(from + ": " + result);
                break;
            case SHA512BASE64:
                beginIndex = SHA512BASE64.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                hash = DigestUtils.sha512(bytes);
                base64 = Base64.encodeBase64(hash);
                result = new String(base64);
                room.sendMessage(from + ": " + result);
                break;
            case MD5:
                beginIndex = MD5.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                result = DigestUtils.md5Hex(bytes);
                room.sendMessage(from + ": " + result);
                break;
            case MD5BASE64:
                beginIndex = MD5BASE64.length() + 1;
                substring = message.substring(beginIndex);
                bytes = substring.getBytes(StandardCharsets.UTF_8);
                hash = DigestUtils.md5(bytes);
                base64 = Base64.encodeBase64(hash);
                result = new String(base64);
                room.sendMessage(from + ": " + result);
                break;
        }
    }
}
