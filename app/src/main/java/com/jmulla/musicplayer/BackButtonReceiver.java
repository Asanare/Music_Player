package com.jmulla.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/***
 * Created by Jamal on 12/08/2016.
 */
//This receiver is used to get the event when the user clicks the backbutton. The back method is then called
public class BackButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CurrentSong.Back();
    }
}
