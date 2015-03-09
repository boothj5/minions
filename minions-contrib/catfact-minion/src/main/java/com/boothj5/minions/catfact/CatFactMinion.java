package com.boothj5.minions.catfact;

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

public class CatFactMinion extends Minion {
    private final ObjectMapper objectMapper;

    public CatFactMinion() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getHelp() {
        return "Get a random cat fact.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "http://catfacts-api.appspot.com/api/facts?number=1";
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            CatFactResponse catFactResponse = objectMapper.readValue(body, CatFactResponse.class);
            if (!"true".equals(catFactResponse.getSuccess())) {
                muc.sendMessage("Could not get cat fact.");
            } else {
                muc.sendMessage(catFactResponse.getFacts().get(0));
            }
        } catch (IOException e) {
            muc.sendMessage("Could not get cat fact.");
        }
    }
}
