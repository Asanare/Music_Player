package com.jmulla.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jamal on 07/12/2016.
 */
public class HeadSetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("Broadcast Receiver", action);
        if ((action.compareTo(Intent.ACTION_HEADSET_PLUG)) == 0)   //if the action match a headset one
        {
            int headSetState = intent.getIntExtra("state", 0);      //get the headset state property
            int hasMicrophone = intent.getIntExtra("microphone", 0);//get the headset microphone property
            if ((headSetState == 0))        //headset was unplugged & has no microphone
            {
                Manager.mp.pause();
                //do whatever
            }
            if ((hasMicrophone == 0)) {
                CurrentSong.makeToast(context, "NO MIC");
            }
        }

    }

}

