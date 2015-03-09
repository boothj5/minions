package com.boothj5.minions.chuck;

import java.util.List;

public class ChuckResponse {
    String type;
    ChuckResponseValue value;

    public ChuckResponse() {
    }

    class ChuckResponseValue {
        int id;
        String joke;
        List<String> categories;

        public ChuckResponseValue() {
        }

        public int getId() {
            return id;
        }

        public String getJoke() {
            return joke;
        }

        public List<String> getCategories() {
            return categories;
        }
    }

    public String getType() {
        return type;
    }

    public ChuckResponseValue getValue() {
        return value;
    }
}
