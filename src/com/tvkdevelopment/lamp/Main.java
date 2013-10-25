package com.tvkdevelopment.lamp;

public class Main {

    public static void main(String[] args) {
        while (true) {
            // Set the light to the average screen colourF
            LightControl.setRGB(ScreenManager.getAverageScreenColour());

            // Wait a bit before repeating the update
            try {
                Thread.sleep(Config.UPDATE_INTERVAL);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
