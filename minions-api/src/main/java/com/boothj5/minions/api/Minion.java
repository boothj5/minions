package com.boothj5.minions.api;

public interface Minion {
    String getCommand();
    String getHelp();
    void onMessage(MinionsRoom muc, String from, String message) throws MinionsException;
}
