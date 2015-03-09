package com.boothj5.minions.ip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IpResponse {
    String ip;

    public IpResponse() {

    }

    public String getIp() {
        return ip;
    }
}
