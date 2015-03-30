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
        return "project branch - Show build status for branch of project";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        HttpClient client = HttpClientBuilder.create().build();

        String[] split = message.trim().split(" ");
        if (split.length != 2) {
            muc.sendMessage("Please enter a project and branch, e.g. boothj5/profanity master");
            return;
        }

        String repository = split[0];
        String branch = split[1];

        String summaryUrl = "https://api.travis-ci.org/repos/" + repository;
        String branchUrl = summaryUrl + "/branches/" + branch;

        try {
            HttpGet summaryGet = new HttpGet(summaryUrl);
            summaryGet.addHeader("User-Agent", "travis-minion/1.0.0");
            summaryGet.addHeader("Accept", "application/vnd.travis-ci.2+json");
            HttpResponse summaryResponse = client.execute(summaryGet);
            String summaryBody = EntityUtils.toString(summaryResponse.getEntity());
            TravisRepoSummary travisRepoSummary = objectMapper.readValue(summaryBody, TravisRepoSummary.class);
            TravisRepoSummary.TravisRepo travisRepo = travisRepoSummary.getRepo();

            HttpGet branchGet = new HttpGet(branchUrl);
            branchGet.addHeader("User-Agent", "travis-minion/1.0.0");
            branchGet.addHeader("Accept", "application/vnd.travis-ci.2+json");

            HttpResponse branchResponse = client.execute(branchGet);
            String branchBody = EntityUtils.toString(branchResponse.getEntity());
            TravisBranchSummary travisBranchSummary = objectMapper.readValue(branchBody, TravisBranchSummary.class);
            TravisBranchSummary.TravisBranch travisBranch = travisBranchSummary.getBranch();
            TravisBranchSummary.TravisCommit travisCommit = travisBranchSummary.getCommit();

            String result = "\n" +
                    "Project: " + travisRepo.getSlug() + " (" + travisRepo.getDescription() + ")\n" +
                    "Result: " + travisBranch.getState() + "\n" +
                    "Duration: " + travisBranch.getDuration() + " seconds\n" +
                    "Commit ID: " + travisCommit.getSha() + "\n" +
                    "Message: " + travisCommit.getMessage() + "\n" +
                    "Author: " + travisCommit.getAuthorName() + " (" + travisCommit.getAuthorEmail() + ")\n" +
                    "Committer: " + travisCommit.getCommitterName() + " (" + travisCommit.getCommitterEmail() + ")\n" +
                    "View diff: " + travisCommit.getCompareUrl();
            muc.sendMessage(result);

        } catch (IOException e) {
            muc.sendMessage("Could not get project.");
            throw new MinionsException(e);
        }
    }
}
