package com.boothj5.minions.httpstatus;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class HttpStatusMinion extends Minion {

    @Override
    public String getHelp() {
        return "[url] - Get the http status code for a URL.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String[] split = StringUtils.split(message, " ");
        String uri = split[0];
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(uri);
            HttpResponse response = client.execute(get);
            response.getEntity().getContent().close();
            muc.sendMessage("Status " + uri + ": " + response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
        } catch (IOException e) {
            muc.sendMessage("Could not connect " + uri + ": " + e.getMessage());
        }
    }
}
