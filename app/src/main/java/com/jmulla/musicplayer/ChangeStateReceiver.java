package com.jmulla.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jamal on 23/07/2016.
 */
public class ChangeStateReceiver extends BroadcastReceiver {
    //receiver that picks up pause/play requests and processes them
    @Override
    public void onReceive(Context context, Intent intent) {
        CurrentSong.changeState(context);
    }
}
