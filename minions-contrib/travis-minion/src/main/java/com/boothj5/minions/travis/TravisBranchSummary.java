package com.boothj5.minions.travis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TravisBranchSummary {
    TravisBranch branch;
    TravisCommit commit;

    public TravisBranch getBranch() {
        return branch;
    }

    public TravisCommit getCommit() {
        return commit;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class TravisBranch {
        String state;
        int duration;

        public TravisBranch() {
        }

        public String getState() {
            return state;
        }

        public int getDuration() {
            return duration;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class TravisCommit {
        String sha;
        String message;

        @JsonProperty("author_name")
        String authorName;

        @JsonProperty("author_email")
        String authorEmail;

        @JsonProperty("committer_name")
        String committerName;

        @JsonProperty("committer_email")
        String committerEmail;

        @JsonProperty("compare_url")
        String compareUrl;

        public TravisCommit() {
        }

        public String getSha() {
            return sha;
        }

        public String getMessage() {
            return message;
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getAuthorEmail() {
            return authorEmail;
        }

        public String getCommitterName() {
            return committerName;
        }

        public String getCommitterEmail() {
            return committerEmail;
        }

        public String getCompareUrl() {
            return compareUrl;
        }
    }
}
