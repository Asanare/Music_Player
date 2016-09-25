package com.jmulla.musicplayer;

import android.app.Service;
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
    private int songPosition = 0;
    private IBinder iBinder = new MusicBinder();
    private boolean isPlaying = false;
    private String lastSongId;
    //Timer timer = new Timer();
    public boolean isPrepared = false;
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
                mp.stop();
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
        //If song has reached the end of the list, go back to the start
        if (songPosition == (Manager.currentSongList.size() - 1)) {
            songPosition = 0;
        }
        if (Manager.currentState == Manager.State.NORMAL)
        {
            songPosition = Manager.currentSongList.indexOf(Manager.currentSong)+1;
            //songPosition++;
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
            songPosition-=2;
        }
        PlaySong(true);
    }

    public void ResumeAudio() {
        mp.start();
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
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        mp.start();
        //playNext();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
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
