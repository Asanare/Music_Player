package com.jmulla.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jamal on 23/07/2016.
 */
public class ChangeStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Intent.", Toast.LENGTH_LONG).show();
        CurrentSong.changeState(context);
    }
}
