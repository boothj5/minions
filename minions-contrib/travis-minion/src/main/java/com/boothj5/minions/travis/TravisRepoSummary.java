package com.boothj5.minions.travis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TravisRepoSummary {
    TravisRepo repo;

    public TravisRepoSummary() {

    }

    public TravisRepo getRepo() {
        return repo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class TravisRepo {
        int id;
        String slug;
        String description;

        @JsonProperty("last_build_id")
        int lastBuildId;

        @JsonProperty("last_build_number")
        String lastBuildNumber;

        @JsonProperty("last_build_state")
        String lastBuildState;

        @JsonProperty("last_build_duration")
        int lastBuildDuration;

        @JsonProperty("last_build_language")
        String lastBuildLanguage;

        @JsonProperty("last_build_started_at")
        String lastBuildStartedAt;

        @JsonProperty("last_build_finished_at")
        String lastBuildFinishedAt;

        @JsonProperty("github_language")
        String githubLanguage;

        public TravisRepo() {

        }

        public int getId() {
            return id;
        }

        public String getSlug() {
            return slug;
        }

        public String getDescription() {
            return description;
        }

        public int getLastBuildId() {
            return lastBuildId;
        }

        public String getLastBuildNumber() {
            return lastBuildNumber;
        }

        public String getLastBuildState() {
            return lastBuildState;
        }

        public int getLastBuildDuration() {
            return lastBuildDuration;
        }

        public String getLastBuildLanguage() {
            return lastBuildLanguage;
        }

        public String getLastBuildStartedAt() {
            return lastBuildStartedAt;
        }

        public String getLastBuildFinishedAt() {
            return lastBuildFinishedAt;
        }

        public String getGithubLanguage() {
            return githubLanguage;
        }
    }


}
