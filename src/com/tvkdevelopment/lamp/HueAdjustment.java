package com.tvkdevelopment.lamp;

/**
 * Helps adjust the hue with an amount relative to the original hue.
 */
class HueAdjustment {

    /** The target on the hue spectrum to change */
    private final float mTarget;

    /** The total range around the target to update the hue for (linearly scaled) */
    private final float mRange;

    /** The lower limit for the hue changes */
    private final float mLowerLimit;

    /** The upper limit for the hue changes */
    private final float mUpperLimit;

    /** The fraction that the hue will have added */
    private final float mOffset;

    /**
     * Prepares an adjustment for the hue.
     * 
     * @param target
     *            The target on the hue spectrum to change from 0 to 1
     * @param range
     *            The total range around the target to update the hue for (linearly scaled)
     * @param offset
     *            The fraction that the hue will have added
     */
    public HueAdjustment(final float target, final float range, final float offset) {
        mTarget = target;
        mRange = range;
        mLowerLimit = target - mRange / 2;
        mUpperLimit = target + mRange / 2;
        mOffset = offset;
    }

    /**
     * Updates the hue based on this adjustment's parameters.
     * 
     * @param hue
     *            The hue from 0 to 1 to update
     * 
     * @return The updated hue value
     */
    public float adjustHue(float hue) {
        // Only adjust hues within the limit
        if (hue > mLowerLimit && hue < mUpperLimit) {
            // Get the hue relative to the target
            float relHue = Math.abs(hue - mTarget);

            // Decrease the hues that are over the target so that it declines properly
            if (relHue > mRange) {
                relHue -= (relHue - mRange);
            }

            // Adjust the hue properly
            hue += relHue * mOffset;
        }

        return hue % 1;
    }
}