package com.jmulla.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jamal on 12/08/2016.
 */
public class BackButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CurrentSong.Back();
    }
}
