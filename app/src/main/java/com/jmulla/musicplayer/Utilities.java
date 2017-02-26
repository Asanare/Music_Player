package com.jmulla.musicplayer;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.util.concurrent.TimeUnit;

/***
 * Created by Jamal on 26/07/2016.
 */
//little utitlity class with a few important misc methods
class Utilities extends ActivityCompat {
    //changes milliseonds to minutes and seconds
    static String getMinutesFromMillis(long millis) {
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

    //sets the theme for a particular activity
    static void setThemeHere(Context context) {
        String theme = PreferenceManager.getDefaultSharedPreferences(context).getString("theme", "0");
        switch (theme) {
            case "0":
                context.setTheme(R.style.AppTheme_Dark);
                break;
            case "1":
                context.setTheme(R.style.AppTheme_Blue);
        }
    }

}
