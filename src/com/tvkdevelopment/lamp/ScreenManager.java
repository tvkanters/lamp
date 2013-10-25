package com.tvkdevelopment.lamp;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Performs tasks related to the screen such as screen capturing.
 */
public class ScreenManager {

    /** The robot used for screen capturing */
    private final static Robot sRobot;

    static {
        // Initialise the final sRobot keeping in mind it can throw an exception
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (final AWTException ex) {
            ex.printStackTrace();
        }
        sRobot = robot;
    }

    /**
     * Captures the screen and calculates the average colour in it.
     * 
     * @return The average screen colour in RGB
     */
    public static int[] getAverageScreenColour() {

        // Capture the screen
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final BufferedImage image = sRobot.createScreenCapture(new Rectangle(screenSize));
        final int numPixels = screenSize.width * screenSize.height;

        // Sum the colour of every pixel
        int red = 0;
        int green = 0;
        int blue = 0;
        for (int x = 0; x < screenSize.width; ++x) {
            for (int y = 0; y < screenSize.height; ++y) {
                int[] rgbArr = splitRGB(image.getRGB(x, y));

                red += rgbArr[0];
                green += rgbArr[1];
                blue += rgbArr[2];
            }
        }

        // Return the average of every colour
        return new int[] { red / numPixels, green / numPixels, blue / numPixels };
    }

    /**
     * Splits an RGB int into red, green and blue ranging from 0 to 255.
     * 
     * @param rgb
     *            The RGB colour
     * 
     * @return THe RGB colour in separated red, green and blue respectively
     */
    public static int[] splitRGB(final int rgb) {
        final int red = (rgb >> 16) & 0xff;
        final int green = (rgb >> 8) & 0xff;
        final int blue = (rgb) & 0xff;

        return new int[] { red, green, blue };
    }
}
