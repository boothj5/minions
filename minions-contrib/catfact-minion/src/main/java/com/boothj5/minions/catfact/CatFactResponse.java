package com.boothj5.minions.catfact;

import java.util.List;

public class CatFactResponse {
    List<String> facts;
    String success;

    public CatFactResponse() {

    }

    public List<String> getFacts() {
        return facts;
    }

    public String getSuccess() {
        return success;
    }
}
