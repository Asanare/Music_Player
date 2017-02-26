package com.jmulla.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

//This activity is where the user can interact with the song playback and can
public class CurrentSong extends AppCompatActivity {
    //define member variables
    static boolean is_paused = false;
    static AudioService audioService;
    static NotificationCompat.Builder notiBuilder;
    static NotificationManager mNotificationManager;
    static Button btn_play;
    static TextView tv_title;
    static TextView tv_artist;
    static SeekBar seekBar;
    static String songArtist;
    static String songDuration;
    static String songAlbum;
    static String song_cover_loc;
    static String songTitle;
    static String songId;
    Button btn_back;
    ImageView imageView;
    Button btn_fwd;
    Switch sw_repeat;
    Switch sw_shuffle;
    Intent playIntent;
    boolean musicBound = false;
    String songLoc;
    private int startPos;

    //method to connect to the service that is playing the music. This lets us interact with the song
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

    //method to change the state of playback.
    public static void changeState(Context context) {
        if (is_paused) {
            createPlaybackNotification(context, 0);
            resume();
            try {
                btn_play.setBackgroundResource(R.drawable.pause_button);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Exception e", e.toString());
            }
            is_paused = false;
        } else {
            createPlaybackNotification(context, 1);
            pause();
            try {
                btn_play.setBackgroundResource(R.drawable.play_button);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Exception e", e.toString());
            }
            is_paused = true;
        }
    }


    //method which creates the notification so the user can interact with the song
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
        Intent resultIntent = new Intent(context, CurrentSong.class);
        resultIntent.putExtra("START_POSITION", Manager.currentSongList.indexOf(Manager.currentSong));
        PendingIntent openSong = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        notiBuilder.setContentIntent(openSong);
        notiBuilder.addAction(R.drawable.back_button, "Previous", backIntent);
        if (state == 0) {
            notiBuilder.addAction(R.drawable.pause_button, "Pause", playIntent);
        } else if (state == 1) {
            notiBuilder.addAction(R.drawable.play_button, "Play", playIntent);
        }
        notiBuilder.addAction(R.drawable.next_button, "Next", nextIntent);
        notiBuilder.setOngoing(true);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notiBuilder.build());
    }

    //method to remove all notifications
    public static void removeAllNotifs() {
        mNotificationManager.cancelAll();
    }

    //resumes playback
    public static void resume() {
        audioService.ResumeAudio();
    }

    //pauses playback
    public static void pause() {
        audioService.PauseAudio();
    }

    //changes the info for the new song
    public static void fillInfo() {
        tv_title.setText(Manager.currentSong.title);
        tv_artist.setText(Manager.currentSong.artist);
        assert seekBar != null;
        seekBar.setMax(Integer.parseInt(Manager.currentSong.duration));

    }

    //convenience method to make a toast
    public static void makeToast(Context context, Object object) {
        try {
            Toast.makeText(context, String.valueOf(object), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            makeToast(context, e);
        }

    }

    //goes to the previous song
    public static void Back() {
        audioService.PreviousSong();
        try {
            fillInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method to play the next song
    public static void Forward() {
        audioService.NextSong();
        try {
            fillInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This method is called after the user picks an image for the album cover. It then updates the song with the new cover
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Bitmap b = Bitmap.createScaledBitmap(selectedImage, imageView.getWidth(), imageView.getHeight(), true);
                Song song = new Song(songId, songTitle, songArtist, songAlbum, imageUri.toString(), songDuration, songLoc);
                new DatabaseHandler(getBaseContext()).updateSong(song);
                imageView.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //method called when this activity is created. Initialises the activity, sets button receivers and
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setThemeHere(this); //sets the theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_song);
        seekBar = (SeekBar) findViewById(R.id.sb_progress);
        final Handler mHandler = new Handler();
        //sets the seekbar position every second
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (AudioService.mp != null) {
                        int mCurrentPosition = AudioService.mp.getCurrentPosition();
                        seekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }
        });
        //assigns views to variables
        tv_title = (TextView) findViewById(R.id.tv_current_title);
        tv_artist = (TextView) findViewById(R.id.tv_current_artist);
        btn_play = (Button) findViewById(R.id.btn_play_pause);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_fwd = (Button) findViewById(R.id.btn_fwd);
        imageView = (ImageView) findViewById(R.id.img_cover_art);
        sw_repeat = (Switch) findViewById(R.id.sw_current_repeat);
        sw_shuffle = (Switch) findViewById(R.id.sw_current_shuffle);
        //starts an activity so the user can choose an image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intent, 1);
            }
        });
        //sets the actions for the repeat button
        sw_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((Manager.currentState == Manager.State.REPEAT_ONE)) {
                    Manager.currentState = Manager.State.NORMAL;
                    buttonView.setChecked(false);
                } else if ((Manager.currentState == Manager.State.NORMAL || Manager.currentState == Manager.State.SHUFFLE)) {
                    Manager.currentState = Manager.State.REPEAT_ONE;
                    buttonView.setChecked(true);
                    makeToast(getApplicationContext(), "REPEATING");
                }
            }
        });
        //sets the actions for the shuffle button
        sw_shuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((Manager.currentState == Manager.State.SHUFFLE)) {
                    Manager.currentState = Manager.State.NORMAL;
                    buttonView.setChecked(false);
                } else if ((Manager.currentState == Manager.State.NORMAL || Manager.currentState == Manager.State.REPEAT_ONE)) {
                    Manager.currentState = Manager.State.SHUFFLE;
                    buttonView.setChecked(true);
                    makeToast(getApplicationContext(), "SHUFFLING");
                }
            }
        });
        //listener for the back button
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back();
                fillInfo();
            }
        });
        //listener for the forward button
        btn_fwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forward();
                fillInfo();
            }
        });
        //assign values to member variables
        Intent intent = getIntent();
        Song CS = Manager.currentSong;
        songId = CS.getId();
        songLoc = CS.getLocation();
        songArtist = CS.getArtist();
        songAlbum = CS.getAlbum();
        song_cover_loc = CS.getCover_loc();
        songDuration = CS.getDuration();
        songTitle = CS.getTitle();
        startPos = intent.getIntExtra("START_POSITION", 0);
        try {
            //set album cover
            final InputStream imageStream = getContentResolver().openInputStream(Uri.parse(song_cover_loc));
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            Bitmap b = Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fillInfo();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (Manager.audioService.isPrepared && fromUser) {
                        Manager.audioService.seekTo(progress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            btn_play.setBackgroundResource(R.drawable.play_button);
        } else {
            createPlaybackNotification(this, 0);
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
