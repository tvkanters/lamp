package com.tvkdevelopment.lamp;

/**
 * Contains several general config parameters for global use.
 */
public class Config {

    /** How often the light is updated in ms */
    public final static int UPDATE_INTERVAL = 100;

    /** How long the light transitions take in steps of 100ms*/
    public final static int TRANSITION_DURATION = 10;

    /** The IP for the lamps */
    public final static String IP = "10.0.1.2";

    /** The username for lamp requests */
    public final static String USERNAME = "appeltaart";

    /** The ID of the light we want to control */
    public final static int LIGHT_ID = 2;

    /** Whether or not we want to actually send requests (false is useful outside the network) */
    public final static boolean LIVE = true;
}
