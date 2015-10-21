package com.boothj5.minions.slangslate;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SlangslateMinion extends Minion {

    private Map<String, String> lastMessages = new HashMap<>();

    @Override
    public String getHelp() {
        return "[term|user] - Translate internet slang term or user's last message";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        lastMessages.put(from, message);
    }

    @Override
    public void onCommand(MinionsRoom muc, String from, String message) throws MinionsException {
        String trimmed = message.trim();
        if ("".equals(trimmed)) {
            muc.sendMessage(from + " nothing doesn't mean anything.");
        } else if (lastMessages.containsKey(trimmed)) {
            String lastMessage = lastMessages.get(trimmed);

            String translatedMessage = "";

            String[] words = lastMessage.split(" ");
            for (String word : words) {
                String translatedWord = getSlang(word);
                if (translatedWord != null) {
                    translatedMessage += translatedWord + " ";
                } else {
                    translatedMessage += word + " ";
                }
            }

            muc.sendMessage(trimmed + " said: " + translatedMessage);
        } else {
            String slang = trimmed.toLowerCase();
            String result = getSlang(slang);

            if (result != null) {
                muc.sendMessage(from + ": " + result);
            } else {
                muc.sendMessage("Soz " + from + ", idk");
            }
        }
    }

    private String getSlang(String slang) {
        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet("http://www.noslang.com/search.php?st=" + slang + "&submit=Search");
            HttpResponse response = client.execute(get);

            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            // <a name="yh"></a><abbr title="yeah"><b>
            String findStart = "<a name=\"" + slang + "\"></a><abbr title=\"";
            String findEnd = "\"><b>" + slang + "</b>";
            int foundStart = responseString.indexOf(findStart);
            int start = foundStart + findStart.length();

            String startRemoved = responseString.substring(start);

            int end = startRemoved.indexOf(findEnd);

            return startRemoved.substring(0, end);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
