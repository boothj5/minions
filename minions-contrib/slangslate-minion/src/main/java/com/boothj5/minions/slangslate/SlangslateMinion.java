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

public class SlangslateMinion extends Minion {
    @Override
    public String getHelp() {
        return "[term] - Translate internet slang";
    }

    @Override
    public void onCommand(MinionsRoom muc, String from, String message) throws MinionsException {
        String trimmed = message.trim();
        if ("".equals(trimmed)) {
            muc.sendMessage(from + " nothing doesn't mean anything.");
        } else {
            try {
                String slang = trimmed.toLowerCase();

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
                String result = startRemoved.substring(0, end);

                muc.sendMessage(from + " said: " + result);
            } catch (IOException | RuntimeException e) {
                muc.sendMessage("Sorry " + from + ", idk");
                throw new MinionsException(e);
            }
        }
    }
}
