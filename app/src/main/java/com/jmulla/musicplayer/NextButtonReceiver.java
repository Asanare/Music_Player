package com.jmulla.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jamal on 12/08/2016.
 */
public class NextButtonReceiver extends BroadcastReceiver {
    //receiver that goes to the next song when the next button is clicked
    @Override
    public void onReceive(Context context, Intent intent) {
        CurrentSong.Forward();
    }
}
