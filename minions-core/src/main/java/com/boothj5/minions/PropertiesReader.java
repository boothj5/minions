package com.boothj5.minions;

public class PropertiesReader {
    public String getUser() {
        return getMandatoryString("minions.user.name");
    }

    public String getService() {
        return getMandatoryString("minions.user.service");
    }

    public String getResource() {
        return getStringWithDefault("minions.user.resource", "minions-core");
    }

    public String getPassword() {
        return getMandatoryString("minions.user.password");
    }

    public String getServer() {
        return getOptionalString("minions.service.server");
    }

    public int getPort() {
        return getIntWithDefault("minions.service.port", 5222);
    }

    public String getRoom() {
        return getMandatoryString("minions.room.jid");
    }

    public String getRoomNick() {
        return getStringWithDefault("minions.room.nick", "minions");
    }

    public int getRefreshSeconds() {
        return getIntWithDefault("minions.refresh.seconds", 10);
    }

    public String getPrefix() {
        return getStringWithDefault("minions.prefix", "!");
    }

    public String getPluginsDir() {
        String defaultValue = System.getProperty("user.home") + "/.local/share/minions/plugins";
        return getStringWithDefault("minions.pluginsdir", defaultValue);
    }

    private static int getIntWithDefault(String property, int defaultValue) {
        String resultStr = System.getProperty(property);
        if (resultStr == null) {
            return defaultValue;
        } else {
            return Integer.valueOf(resultStr);
        }
    }

    private static String getMandatoryString(String property) {
        String result = System.getProperty(property);
        if (result == null) {
            throw new IllegalStateException("Property " + property + " not set.");
        } else {
            return result;
        }
    }

    private static String getStringWithDefault(String property, String defaultValue) {
        String result = System.getProperty(property);
        if (result == null) {
            result = defaultValue;
        }
        return result;

    }

    private static String getOptionalString(String property) {
        return System.getProperty(property);
    }

    public String getRoomPassword() {
        return getOptionalString("minions.room.password");
    }
}
