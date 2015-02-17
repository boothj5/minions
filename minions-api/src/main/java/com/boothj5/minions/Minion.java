package com.boothj5.minions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Minion {
    private static final Logger LOG = LoggerFactory.getLogger(Minion.class);

    public abstract String getCommand();
    public abstract String getHelp();
    public abstract void onMessage(MinionsRoom muc, String from, String message) throws MinionsException;

    void onMessageWrapper(MinionsRoom muc, String from, String message) {
        try {
            onMessage(muc, from, message);
        } catch (RuntimeException rte) {
            LOG.error("Minions RuntimeException", rte);
        } catch (MinionsException me) {
            LOG.error("MinionsException", me);
        }
    }
}
