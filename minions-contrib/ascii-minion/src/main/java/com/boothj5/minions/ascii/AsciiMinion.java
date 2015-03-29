package com.boothj5.minions.ascii;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AsciiMinion extends Minion {

    @Override
    public String getHelp() {
        return "text - Render text in ASCII";
    }

    @Override
    public void onMessage(MinionsRoom minionsRoom, String from, String message) throws MinionsException {
        String response = writeMessage(message);
        minionsRoom.sendMessage(response);
    }

    private String writeMessage(String message) {
        BufferedImage image = new BufferedImage(144, 32, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.drawString(message, 6, 24);

        StringBuilder sbMain = new StringBuilder();
        sbMain.append("\n");
        for (int y = 0; y < 32; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < 144; x++) {
                sb.append(image.getRGB(x, y) == -16777216 ? " " : image.getRGB(x, y) == -1 ? "#" : "*");
            }
            if (sb.toString().trim().isEmpty()) {
                continue;
            }
            sbMain.append(sb.toString()).append("\n");
        }
        return sbMain.toString();
    }
}
