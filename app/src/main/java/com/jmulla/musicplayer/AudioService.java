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

public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    public static MediaPlayer mp;
    //Timer timer = new Timer();
    public boolean isPrepared = false;
    private int songPosition = 0;
    private IBinder iBinder = new MusicBinder();
    private boolean isPlaying = false;
    private String lastSongId;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    CurrentSong.makeToast(getApplicationContext(), "AUDIOFOCUS_GAIN");
                    //restart/resume your sound
                    CurrentSong.changeState(getApplicationContext());
                    mp.setVolume(1, 1);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    CurrentSong.makeToast(getApplicationContext(), "AUDIOFOCUS_LOSS");
                    //CurrentSong.changeState(getApplicationContext());
                    mp.stop();
                    //Loss of audio focus for a long time
                    //Stop playing the sound
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
                    //But one can duck. Lower the volume of playing the sound
                    mp.setVolume(0.5f, 0.5f);
                    break;

                default:
                    //
            }
        }
    };

    //boolean isTimerWorking = false;
    public AudioService() {
    }

    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mp = new MediaPlayer();
        initialiseMediaPlayer();

    }

    public void initialiseMediaPlayer() {
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    public void seekTo(int msec) {
        mp.seekTo(msec);
    }

    public void PlaySongList(int pos) {
        songPosition = pos;
        Manager.currentSong = Manager.currentSongList.get(songPosition);
        PlaySong(false);
    }


    public void PlaySong(boolean stop) {
        Manager.currentSong = Manager.currentSongList.get(songPosition);
        if (!isPlaying) {
            //CurrentSong.changeState(getBaseContext());
            CurrentSong.createPlaybackNotification(getBaseContext(), 0);
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                }

                mp.reset();
                mp.setDataSource(Manager.currentSong.location);
                mp.prepareAsync();
                isPlaying = true;
                lastSongId = Manager.currentSong.id;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isPlaying && stop) {
            mp.stop();
            isPlaying = false;

            /*Intent intent = new Intent(getBaseContext(), CurrentSong.class);
            intent.putExtra("START_POSITION", songPosition);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getBaseContext().startActivity(intent);*/

            PlaySong(false);
        }
        if (isPlaying && !(lastSongId.equals(Manager.currentSong.id))) {
            mp.stop();
            isPlaying = false;
            PlaySong(false);
        }


    }

    public void NextSong() {
        //TODO // FIXME: 28/09/2016 Fix song positions
        //If song has reached the end of the list, go back to the start
        if ((songPosition == (Manager.currentSongList.size()) - 1) && Manager.currentState == Manager.State.NORMAL) {
            songPosition = 0;
        } else if (Manager.currentState == Manager.State.NORMAL) {
            songPosition = Manager.currentSongList.indexOf(Manager.currentSong)+1;
        }

        else if (Manager.currentState == Manager.State.SHUFFLE){
            Random rand = new Random();
            songPosition = rand.nextInt(Manager.currentSongList.size());
        }
        else if (Manager.currentState == Manager.State.REPEAT_ONE){
            Log.d("INFO", "Repeating one song");
        }
        PlaySong(true);
    }

    public void PreviousSong() {
        CurrentSong.makeToast(getBaseContext(), songPosition);
        if (songPosition == 0) {
            songPosition = Manager.currentSongList.size() -1;
        }else if(songPosition == 1){
            songPosition = 0;
        }
        else {
            songPosition -= 1;
        }
        PlaySong(true);
    }

    public void ResumeAudio() {
        try {
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
/*        if (!isTimerWorking) {
            timer = new Timer();
            scheduleSong(mp.getDuration() - mp.getCurrentPosition());
            isTimerWorking = true;
        }*/

        CurrentSong.makeToast(getBaseContext(), "Resumed");
    }

    public void PauseAudio() {
        CurrentSong.makeToast(getBaseContext(), "Paused");
        mp.pause();
/*        if (isTimerWorking){
            try {
                timer.cancel();
                isTimerWorking = false;
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }*/

    }

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

    void releaseAudioFocusForMyApp(final Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i("I", "DEAD");
        releaseAudioFocusForMyApp(getApplicationContext());
        super.onDestroy();
        isPlaying = false;
        //timer.cancel();
        mp.stop();
        mp.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        isPrepared = true;
        //mp.start();
        if (requestAudioFocusForMyApp(getApplicationContext())) {
            player.start();
        }
        //playNext();
    }

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
