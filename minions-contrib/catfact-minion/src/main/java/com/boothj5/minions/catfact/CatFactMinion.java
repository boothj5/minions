package com.boothj5.minions.catfact;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CatFactMinion extends Minion {
    private final ObjectMapper objectMapper;

    public CatFactMinion(MinionsRoom room) {
        super(room);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getHelp() {
        return "Get a random cat fact.";
    }

    @Override
    public void onCommand(String from, String message) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "http://catfacts-api.appspot.com/api/facts?number=1";
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            CatFactResponse catFactResponse = objectMapper.readValue(body, CatFactResponse.class);
            if (!"true".equals(catFactResponse.getSuccess())) {
                room.sendMessage("Could not get cat fact.");
            } else {
                room.sendMessage(catFactResponse.getFacts().get(0));
            }
        } catch (IOException e) {
            room.sendMessage("Could not get cat fact.");
        }
    }
}
