package com.jmulla.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CurrentSong extends AppCompatActivity {
    static boolean is_paused = false;
    static AudioService audioService;
    static NotificationCompat.Builder notiBuilder;
    static NotificationManager mNotificationManager;
    static RemoteViews remoteViews;
    static Button btn_play;

    Button btn_back;
    Button btn_fwd;
    Button btn_repeat;
    Button btn_shuffle;
    static TextView tv_title;
    static TextView tv_artist;
    Intent playIntent;
    static SeekBar seekBar;

    boolean musicBound = false;
    String songLoc;
    static String songArtist;
    static String songDuration;
    static String songTitle;
    static String songId;
    public ArrayList<Song> allSongs;
    private int startPos;

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.MusicBinder binder = (AudioService.MusicBinder) service;
            audioService = binder.getService();
            Manager.audioService = audioService;
            audioService.PlaySongList(startPos);
            //audioService.PlayAudio(songLoc, songId);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;

        }
    };

    public static void changeState(Context context) {
        if (is_paused) {
            createPlaybackNotification(context, 0);
            //createPauseNotification(context);
            resume();
            try {
                btn_play.setBackgroundResource(R.drawable.pause_button);
            } catch (Exception e) {
                Log.d("Exception e", e.toString());
            }
            is_paused = false;
        } else {
            createPlaybackNotification(context, 1);
            //createPlayNotification(context);
            pause();
            try {
                btn_play.setBackgroundResource(R.drawable.play_button);
            } catch (Exception e) {
                Log.d("Exception e", e.toString());
            }
            is_paused = true;
        }
    }

    /*    public static void createPauseNotification(Context context) {
            Intent play = new Intent(context, MyReceiver.class);
            play.setAction("com.jmulla.musicplayer.PLAYBUTTONCLICKED");
            PendingIntent playIntent = PendingIntent.getBroadcast(context, 1, play, PendingIntent.FLAG_CANCEL_CURRENT);
            //remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            //remoteViews.setOnClickPendingIntent(R.id.btn_not_play,playIntent);
            notiBuilder = new NotificationCompat.Builder(context);
            notiBuilder.setSmallIcon(R.drawable.refresh_icon);
            //notiBuilder.setContent(remoteViews);
            notiBuilder.setContentTitle(songTitle);
            notiBuilder.setContentText(songArtist);
            notiBuilder.setContentInfo(songDuration);
            notiBuilder.setWhen(0);
            notiBuilder.addAction(R.drawable.pause_button, "Pause", playIntent);
            //notiBuilder.addAction(R.drawable.next_button, "Next", playIntent);
            notiBuilder.setOngoing(true);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, notiBuilder.build());
        }*/
    public static void createPlaybackNotification(Context context, int state) {
        Intent play = new Intent(context, ChangeStateReceiver.class);
        play.setAction("com.jmulla.musicplayer.CHANGE_STATE_BUTTON_CLICKED");
        PendingIntent playIntent = PendingIntent.getBroadcast(context, 1, play, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent next = new Intent(context, NextButtonReceiver.class);
        next.setAction("com.jmulla.musicplayer.NEXT_BUTTON_CLICKED");
        PendingIntent nextIntent = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent back = new Intent(context, BackButtonReceiver.class);
        back.setAction("com.jmulla.musicplayer.BACK_BUTTON_CLICKED");
        PendingIntent backIntent = PendingIntent.getBroadcast(context, 2, back, PendingIntent.FLAG_CANCEL_CURRENT);
        notiBuilder = new NotificationCompat.Builder(context);
        notiBuilder.setSmallIcon(R.drawable.refresh_icon);
        notiBuilder.setContentTitle(Manager.currentSong.title);
        notiBuilder.setContentText(Manager.currentSong.artist);
        notiBuilder.setContentInfo(Utilities.getMinutesFromMillis(Long.parseLong(Manager.currentSong.duration)));
        notiBuilder.setWhen(0);
        //notiBuilder.addAction(R.drawable.next_button, "Next", nextIntent);


        if (state == 0) {
            notiBuilder.addAction(R.drawable.pause_button, "Pause", playIntent);
        } else if (state == 1) {
            notiBuilder.addAction(R.drawable.play_button, "Play", playIntent);
        }
        //notiBuilder.addAction(R.drawable.back_button, "Previous", backIntent);
        notiBuilder.setOngoing(true);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notiBuilder.build());
    }
    public static void removeAllNotifs() {
        mNotificationManager.cancelAll();
    }

/*    public static void createPlayNotification(Context context) {
        Intent play = new Intent(context, MyReceiver.class);
        play.setAction("com.jmulla.musicplayer.PLAYBUTTONCLICKED");
        PendingIntent playIntent = PendingIntent.getBroadcast(context, 1, play, PendingIntent.FLAG_CANCEL_CURRENT);
        //remoteViews = new RemoteViews(context.getPackageName(),R.layout.notification_layout);
        //remoteViews.setOnClickPendingIntent(R.id.btn_not_play,playIntent);
        notiBuilder = new NotificationCompat.Builder(context);
        //notiBuilder.setContent(remoteViews);
        notiBuilder.setSmallIcon(R.drawable.refresh_icon);
        notiBuilder.setContentTitle(songTitle);
        notiBuilder.setContentText(songArtist);
        notiBuilder.setContentInfo(songDuration);
        notiBuilder.setWhen(0);
        notiBuilder.addAction(R.drawable.play_button, "Play", playIntent);
        //notiBuilder.addAction(R.drawable.next_button, "Next", playIntent);
        notiBuilder.setOngoing(true);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notiBuilder.build());
    }*/

    public static void resume() {
        audioService.ResumeAudio();
    }

    public static void pause() {
        audioService.PauseAudio();
    }

    public static void fillInfo(){

        tv_title.setText(Manager.currentSong.title);
        tv_artist.setText(Manager.currentSong.artist);
        assert seekBar!= null;
        seekBar.setMax(Integer.parseInt(Manager.currentSong.duration));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_song);
        seekBar = (SeekBar) findViewById(R.id.sb_progress);

        final Handler mHandler = new Handler();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(AudioService.mp != null){
                    int mCurrentPosition = AudioService.mp.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        tv_title = (TextView) findViewById(R.id.tv_current_title);
        tv_artist = (TextView) findViewById(R.id.tv_current_artist) ;
        btn_play = (Button) findViewById(R.id.btn_play_pause);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_fwd = (Button) findViewById(R.id.btn_fwd);
        btn_repeat = (Button) findViewById(R.id.btn_current_repeat);
        btn_shuffle = (Button) findViewById(R.id.btn_current_shuffle);
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Manager.currentState == Manager.State.REPEAT_ONE){
                    Manager.currentState = Manager.State.NORMAL;
                }
                else if(Manager.currentState == Manager.State.NORMAL || Manager.currentState == Manager.State.SHUFFLE){
                    Manager.currentState = Manager.State.REPEAT_ONE;
                    makeToast(getApplicationContext(), "REPEATING");
                }
            }
        });
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Manager.currentState == Manager.State.SHUFFLE){
                    Manager.currentState = Manager.State.NORMAL;
                }
                else if (Manager.currentState == Manager.State.NORMAL || Manager.currentState == Manager.State.REPEAT_ONE){
                    Manager.currentState = Manager.State.SHUFFLE;
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back();
            }
        });
        btn_fwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forward();
            }
        });
        Intent intent = getIntent();
        /*songId = intent.getStringExtra("SONG_ID");
        songLoc = intent.getStringExtra("SONG_LOCATION");
        songArtist = intent.getStringExtra("SONG_ARTIST");
        songDuration = intent.getStringExtra("SONG_DURATION");
        songTitle = intent.getStringExtra("SONG_TITLE");*/
        Song CS = Manager.currentSong;
        songId = CS.id;
        songLoc = CS.location;
        songArtist = CS.artist;
        songDuration = CS.duration;
        songTitle = CS.title;
        startPos = intent.getIntExtra("START_POSITION", 0);
        //allSongs = (ArrayList<Song>) intent.getExtras().getSerializable("Songs");
        fillInfo();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Manager.audioService.isPrepared && fromUser) {
                    Manager.audioService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (is_paused) {
            createPlaybackNotification(this, 1);
            //createPlayNotification(this);
            btn_play.setBackgroundResource(R.drawable.play_button);
        } else {
            createPlaybackNotification(this, 0);
            //createPauseNotification(this);
            btn_play.setBackgroundResource(R.drawable.pause_button);
        }
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState(getBaseContext());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fillInfo();
    }

    private void Back() {
        audioService.PreviousSong();
    }
    private void Forward(){
        audioService.NextSong();
    }
    public static void makeToast(Context context, Object object) {
        try {
            Toast.makeText(context, String.valueOf(object), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            makeToast(context, e);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(this, AudioService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            makeToast(this, "Bound");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
            makeToast(this, "Unbound");
        }
    }


}
