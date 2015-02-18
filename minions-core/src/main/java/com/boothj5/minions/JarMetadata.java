package com.boothj5.minions;

public class JarMetadata {
    private final Long timestamp;
    private final String command;
    private final String className;

    public JarMetadata(Long timestamp, String command, String className) {
        this.timestamp = timestamp;
        this.command = command;
        this.className = className;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getClassName() {
        return className;
    }
}
