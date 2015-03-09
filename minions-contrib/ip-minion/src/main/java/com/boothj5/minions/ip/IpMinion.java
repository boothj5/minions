package com.boothj5.minions.ip;

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
import java.net.*;
import java.util.Enumeration;

public class IpMinion extends Minion {
    private final ObjectMapper objectMapper;

    public IpMinion() {
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public String getHelp() {
        return "IP addresses.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        muc.sendMessage("");
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "http://jsonip.com/";
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            IpResponse ipResponse = objectMapper.readValue(body, IpResponse.class);
            muc.sendMessage("Internet IP Address: " + ipResponse.getIp());
        } catch (IOException e) {
            muc.sendMessage("Could not get external IP address.");
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                String displayName = networkInterface.getDisplayName();
                if (!"lo".equals(displayName)) {
                    muc.sendMessage("Interface " + displayName + ":");
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        String inetAddressString = inetAddress.toString();
                        if (inetAddressString.startsWith("/")) {
                            inetAddressString = inetAddressString.substring(1);
                        }
                        if (inetAddress instanceof Inet6Address) {
                            muc.sendMessage("  IPv6 Address: " + inetAddressString);
                        } else if (inetAddress instanceof Inet4Address) {
                            muc.sendMessage("  IPv4 Address: " + inetAddressString);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            muc.sendMessage("Could not get local IP address.");
        }
    }
}
