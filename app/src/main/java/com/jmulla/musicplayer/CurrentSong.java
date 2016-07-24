package com.jmulla.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class CurrentSong extends AppCompatActivity {
    static boolean is_paused = false;
    static AudioService audioService;
    static NotificationCompat.Builder notiBuilder;
    static NotificationManager mNotificationManager;
    static RemoteViews remoteViews;
    Button btn_play, btn_back, btn_fwd;
    Intent playIntent;
    boolean musicBound = false;
    String songLoc;
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.MusicBinder binder = (AudioService.MusicBinder) service;
            audioService = binder.getService();
            audioService.PlayAudio(songLoc);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;

        }
    };

    public static void changeState(Context context) {
        removeAllNotifs();
        if (is_paused) {
            createPauseNotification(context);
            resume();
            is_paused = false;
        } else {
            createPlayNotification(context);
            pause();
            is_paused = true;
        }
    }

    public static void createPauseNotification(Context context) {
        Intent play = new Intent(context, MyReceiver.class);
        play.setAction("com.jmulla.musicplayer.PLAYBUTTONCLICKED");
        PendingIntent playIntent = PendingIntent.getBroadcast(context, 1, play, PendingIntent.FLAG_CANCEL_CURRENT);
        //remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        //remoteViews.setOnClickPendingIntent(R.id.btn_not_play,playIntent);
        notiBuilder = new NotificationCompat.Builder(context);
        notiBuilder.setSmallIcon(R.drawable.refresh_icon);
        //notiBuilder.setContent(remoteViews);

        notiBuilder.addAction(R.drawable.pause_button, "Pause", playIntent);
        notiBuilder.setOngoing(true);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notiBuilder.build());
    }

    public static void removeAllNotifs() {
        mNotificationManager.cancelAll();
    }

    public static void createPlayNotification(Context context) {
        Intent play = new Intent(context, MyReceiver.class);
        play.setAction("com.jmulla.musicplayer.PLAYBUTTONCLICKED");
        PendingIntent playIntent = PendingIntent.getBroadcast(context, 1, play, PendingIntent.FLAG_CANCEL_CURRENT);
        //remoteViews = new RemoteViews(context.getPackageName(),R.layout.notification_layout);
        //remoteViews.setOnClickPendingIntent(R.id.btn_not_play,playIntent);
        notiBuilder = new NotificationCompat.Builder(context);
        //notiBuilder.setContent(remoteViews);
        notiBuilder.setSmallIcon(R.drawable.refresh_icon);

        notiBuilder.addAction(R.drawable.play_button, "Play", playIntent);
        notiBuilder.setOngoing(true);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notiBuilder.build());
    }

    public static void resume() {
        audioService.ResumeAudio();
    }

    public static void pause() {
        audioService.PauseAudio();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_song);
        btn_play = (Button) findViewById(R.id.btn_play_pause);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_fwd = (Button) findViewById(R.id.btn_fwd);
        Intent intent = getIntent();
        songLoc = intent.getStringExtra("SONG_LOCATION");
        createPauseNotification(this);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Clicked", Toast.LENGTH_SHORT).show();
                if (is_paused) {
                    resume();
                    btn_play.setText("Pause");
                    is_paused = false;
                } else {
                    resume();
                    btn_play.setText("Play");
                    is_paused = true;
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(this, AudioService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
        }
    }


}
