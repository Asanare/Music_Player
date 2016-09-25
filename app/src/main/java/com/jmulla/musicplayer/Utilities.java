package com.jmulla.musicplayer;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jamal on 26/07/2016.
 */
public class Utilities {
    public static String getMinutesFromMillis(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");

        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String duration = "";
        if (String.valueOf(minutes).length() == 1) {
            duration += "0" + String.valueOf(minutes) + ":";
        } else if (String.valueOf(minutes).length() == 2) {
            duration += String.valueOf(minutes) + ":";
        }
        if (String.valueOf(seconds).length() == 1) {
            duration += "0" + String.valueOf(seconds);
        } else if (String.valueOf(seconds).length() == 2) {
            duration += String.valueOf(seconds);
        } else {
            duration = "00:00";
        }
        return String.valueOf(duration);
    }

    public static float getSecondsFromMillis(long millis){
        return ((float) millis * (float) 0.001);
    }
}
