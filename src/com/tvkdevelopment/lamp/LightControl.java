package com.tvkdevelopment.lamp;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;

/**
 * Used for communication with the lights, such as changing the colours.
 */
public class LightControl {

    /** The offset that gets applied to the hue to correct the colours */
    private final static int HUE_OFFSET = 2500;

    /** The top limit of the hue spectrum */
    private final static int HUE_MAX = 65535;

    /** How much the saturation will be increased */
    private final static int SATURATION_MULTIPLIER = 2;

    /** The different adjustment placed on the hue per colour */
    private final static HueAdjustment[] sAdjustments = {
            // Fix yellows
            new HueAdjustment(0.2f, 0.2f, 0.4f),

            // Fix greens
            new HueAdjustment(0.3f, 0.2f, 0.3f),

            // Fix blues
            new HueAdjustment(0.26f, 0.2f, 0.3f) };

    /**
     * Requests the light to have its colour changed to the specified RGB colour.
     * 
     * @param rgb
     *            The RGB colours where the first index is red from 255, the second is green and the
     *            third is blue
     */
    public static void setRGB(final int[] rgb) {

        // Convert RGB to HSB
        final float[] hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], new float[3]);

        // Adjust hue for certain colours to correct the spectrum
        float hueFloat = hsb[0];
        for (final HueAdjustment hueAdjustment : sAdjustments) {
            hueFloat = hueAdjustment.adjustHue(hueFloat);
        }
        int hue = (int) (hsb[0] * HUE_MAX);
        hue = ((hue + HUE_OFFSET) % HUE_MAX);

        // Prepare saturation and brightness
        int saturation = (int) (Math.min(1, hsb[1] * SATURATION_MULTIPLIER) * 255);
        int brightness = (int) (hsb[2] * 255);

        // Update the light colour
        for(int light_ID : Config.LIGHT_ID)
        {
            setHSB(hue, saturation, brightness, light_ID);
        }
    }

    /**
     * Requests the light to have its colour changed to the specified HSB colour.
     * 
     * @param hue
     *            The hue from 0 to 1
     * @param saturation
     *            The saturation from 0 to 1
     * @param brightness
     *            The brightness from 0 to 1
     */
    // For JSONObject sloppiness
    @SuppressWarnings("unchecked")
    public static void setHSB(final int hue, final int saturation, final int brightness, int light_ID) {

        // Prepare the parameters for the request
        JSONObject parameters = new JSONObject();
        parameters.put("colormode", "hs");
        parameters.put("hue", hue);
        parameters.put("sat", saturation);
        parameters.put("bri", brightness);
        parameters.put("transitiontime", Config.TRANSITION_DURATION);
        final String parameterString = parameters.toJSONString();

        System.out.println(parameterString);

        if (Config.LIVE) {
            HttpURLConnection connection = null;
            DataOutputStream writer = null;
            BufferedReader reader = null;
            try {
                // Prepare the connection
                final URL url = new URL("http://" + Config.IP + "/api/" + Config.USERNAME
                        + "/lights/" + light_ID + "/state");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length",
                        "" + Integer.toString(parameterString.getBytes().length));
                connection.setUseCaches(false);

                // Send the parameters
                writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes(parameterString);
                writer.flush();

                // Print the response
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    System.out.println(inputLine);
                }
                reader.close();

            } catch (final IOException ex) {
                ex.printStackTrace();

            } finally {
                // Close all connections and buffers
                if (connection != null) {
                    connection.disconnect();
                }
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {}
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
