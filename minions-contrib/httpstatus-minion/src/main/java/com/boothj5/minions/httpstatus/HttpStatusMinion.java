package com.boothj5.minions.httpstatus;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
import com.boothj5.minions.api.MinionsRoom;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class HttpStatusMinion implements Minion {
    public static final String COMMAND = "status";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " [url] - Get the http status code for a URL.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String[] split = StringUtils.split(message, " ");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(split[1]);
        try {
            HttpResponse response = client.execute(get);
            response.getEntity().getContent().close();
            muc.sendMessage("Status " + split[1] + ": " + response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
        } catch (IOException e) {
            e.printStackTrace();
            muc.sendMessage("Could not connect " + split[1] + ": " + e.getMessage());
        }
    }
}
