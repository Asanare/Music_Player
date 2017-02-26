package com.jmulla.musicplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.Random;

//This is where all the song playback happens. This service allows for music to play in the background
public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    public static MediaPlayer mp;  //the mediaplayer that actually plays the music
    public boolean isPrepared = false;
    private int songPosition = 0;
    private IBinder iBinder = new MusicBinder();  //the binder used to bind to an activity
    private boolean isPlaying = false;
    private String lastSongId;
    //the following listener is used to respond to events such as when the audio focus is lost.
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN: //we got the audiofocus so we can play the song
                    CurrentSong.changeState(getApplicationContext());
                    mp.setVolume(1, 1);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS: //system says we've lost focus
                    //Loss of audio focus for a long time
                    //Stop playing the sound
                    mp.stop();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    CurrentSong.makeToast(getApplicationContext(), "AUDIOFOCUS_LOSS_TRANSIENT");
                    CurrentSong.changeState(getApplicationContext());
                    //Loss of audio focus for a short time
                    //Pause playing the sound
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    CurrentSong.makeToast(getApplicationContext(), "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    //Loss of audio focus for a short time.
                    //But we can duck. Lower the volume of the song
                    mp.setVolume(0.5f, 0.5f);
                    break;

                default:
                    mp.stop();
            }
        }
    };

    //empty constructor//
    public AudioService() {
    }

    //method to initialise this service. Called when the service is created
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mp = new MediaPlayer(); //create a new mediaplayer and assign to mp
        initialiseMediaPlayer();
    }

    //set the necessary methods to what they need to be
    public void initialiseMediaPlayer() {
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK); //get wakelock so the service isn't killed
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    //method to go to a specific position in the song
    public void seekTo(int msec) {
        mp.seekTo(msec);
    }

    //method which is used to play a list of songs. Handles going backwards and forwards
    public void PlaySongList(int pos) {
        songPosition = pos;
        Manager.currentSong = Manager.currentSongList.get(songPosition);
        PlaySong(false);
    }

    //recursive method which handles the actual playing of a song
    public void PlaySong(boolean stop) {
        Manager.currentSong = Manager.currentSongList.get(songPosition); //sets the current song
        if (!isPlaying) {
            //if nothing is already playing, prepare and play the song
            CurrentSong.createPlaybackNotification(getBaseContext(), 0);
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.reset();
                mp.setDataSource(Manager.currentSong.location); //song to play
                mp.prepareAsync();
                isPlaying = true;
                lastSongId = Manager.currentSong.id; //last song played
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //if it is playing but we want to reset the mediaplayer, stop the mediaplayer and call the method again
        if (isPlaying && stop || isPlaying && !(lastSongId.equals(Manager.currentSong.id))) {
            mp.stop();
            isPlaying = false;
            PlaySong(false); //call this method again
        }
    }

    //method which figures out what song should be played next
    public void NextSong() {
        //If song has reached the end of the list, go back to the start
        if ((songPosition == (Manager.currentSongList.size()) - 1) && Manager.currentState == Manager.State.NORMAL) {
            songPosition = 0;
        } else if (Manager.currentState == Manager.State.NORMAL) {
            songPosition = Manager.currentSongList.indexOf(Manager.currentSong) + 1;
        } else if (Manager.currentState == Manager.State.SHUFFLE) {
            Random rand = new Random();
            songPosition = rand.nextInt(Manager.currentSongList.size());
        } else if (Manager.currentState == Manager.State.REPEAT_ONE) {
        }
        PlaySong(true);
    }

    //method which figures out what song should be played if the user presses the previous button
    public void PreviousSong() {
        //if we're at the first song already, go to the last song
        if (songPosition == 0) {
            songPosition = Manager.currentSongList.size() - 1;
        } else {
            songPosition -= 1;
        }
        PlaySong(true);
    }

    //Resume the mediaplayer
    public void ResumeAudio() {
        try {
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void PauseAudio() {
        //pause the mediaplayer
        mp.pause();
    }

    //method to request the system for audiofocus. Need this to be able to play
    private boolean requestAudioFocusForMyApp(final Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for playback
        int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("AudioFocus", "Audio focus received");
            return true;
        } else {
            Log.d("AudioFocus", "Audio focus NOT received");
            return false;
        }
    }

    //tell the system we're done with playing and give audiofocus back
    void releaseAudioFocusForMyApp(final Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    //method that returns the current binder
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    //Method called when this service is about to be killed
    @Override
    public void onDestroy() {
        //release audio focus and release the media player
        releaseAudioFocusForMyApp(getApplicationContext());
        isPlaying = false;
        mp.stop();
        mp.release();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    //method which checks if we have audio focus and starts song playback if we do
    @Override
    public void onPrepared(MediaPlayer player) {
        isPrepared = true;
        if (requestAudioFocusForMyApp(getApplicationContext())) {
            player.start();
        }
    }

    //method which plays the next song once this one is complete
    @Override
    public void onCompletion(MediaPlayer player) {
        isPlaying = false;
        NextSong();
        CurrentSong.fillInfo();
    }

    public class MusicBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }
}
