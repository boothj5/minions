package com.boothj5.minions.travis;

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

public class TravisMinion extends Minion {

    private final ObjectMapper objectMapper;

    public TravisMinion () {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getHelp() {
        return "project - Show build status of project";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        HttpClient client = HttpClientBuilder.create().build();
        String url = "https://api.travis-ci.org/repos/" + message;

        try {
            HttpGet get = new HttpGet(url);
            get.addHeader("Accept", "application/vnd.travis-ci.2+json");
            HttpResponse response = client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            TravisResponse travisResponse = objectMapper.readValue(body, TravisResponse.class);
            TravisResponse.TravisRepo repo = travisResponse.getRepo();
            String result = "\n" +
                    "Project: " + repo.getSlug() + " (" + repo.getDescription() + ")\n" +
                    "Build number: " + repo.getLastBuildNumber() + "\n" +
                    "Result: " + repo.getLastBuildState() + "\n" +
                    "Duration: " + repo.getLastBuildDuration() + " seconds";
            muc.sendMessage(result);
        } catch (IOException e) {
            muc.sendMessage("Could not get project.");
            throw new MinionsException(e);
        }
    }
}
