package com.boothj5.minions.chuck;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ChuckMinion extends Minion {
    private final ObjectMapper objectMapper;

    public ChuckMinion(MinionsRoom room) {
        super(room);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getHelp() {
        return "[firstname] [lastname] - Get a joke from Chuck Norris";
    }

    @Override
    public void onCommand(String from, String message) {
        HttpClient client = HttpClientBuilder.create().build();
        String url = "http://api.icndb.com/jokes/random";

        try {
            String[] words = message.trim().split(" ");
            if (words.length >= 1) {
                url += "?firstName=" + words[0];
            }
            if (words.length >= 2) {
                url += "&lastName=" + words[1];
            }
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            ChuckResponse chuckResponse = objectMapper.readValue(body, ChuckResponse.class);
            if (chuckResponse.getType().equals("success")) {
                String joke = chuckResponse.getValue().getJoke();
                String unescaped = joke.replace("&quot;", "\"");
                room.sendMessage(unescaped);
            } else {
                room.sendMessage("Could not find Chuck.");
            }
        } catch (IOException e) {
            room.sendMessage("Could not find Chuck.");
            throw new MinionsException(e);
        }
    }
}
