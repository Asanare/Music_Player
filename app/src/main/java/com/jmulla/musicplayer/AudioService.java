package com.jmulla.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    private MediaPlayer mp;
    private int songPosition = 0;
    private IBinder iBinder = new MusicBinder();
    private boolean isPlaying = false;
    private String lastSongId;
    Timer timer = new Timer();
    boolean isTimerWorking = false;
    public AudioService() {
    }
    public void onCreate(){
        super.onCreate();
        songPosition=0;
        mp = new MediaPlayer();
        initialiseMediaPlayer();
    }
    public void initialiseMediaPlayer(){
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    public void PlaySongList(int pos) {
        songPosition = pos;
        Manager.currentSong = Manager.currentSongList.get(songPosition);
        PlaySong(Manager.currentSong.location, Manager.currentSong.id);
    }

    public void playNext() {
        scheduleSong(mp.getDuration());
    }

    public void scheduleSong(int delay) {
        isTimerWorking = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (songPosition == (Manager.currentSongList.size() - 1)) {
                    songPosition = 0;
                } else {
                    songPosition++;
                }
                Manager.currentSong = Manager.currentSongList.get(songPosition);
                PlaySong(Manager.currentSong.location, Manager.currentSong.id);
            }
        }, delay + 10);
    }

    public void PlaySong(String location, String songId) {
        if (!isPlaying){
            //CurrentSong.changeState(getBaseContext());
            CurrentSong.createPlaybackNotification(getBaseContext(), 0);

            try {
                mp.reset();
                mp.setDataSource(location);
                mp.prepareAsync();
                isPlaying = true;
                lastSongId = songId;
                CurrentSong.seekBar.setMax(mp.getDuration());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isPlaying && !(lastSongId.equals(songId))) {
            mp.stop();
            isPlaying = false;
            PlaySong(location, songId);
        }
    }
    public  void ResumeAudio(){
        mp.start();
        if (!isTimerWorking) {
            timer = new Timer();
            scheduleSong(mp.getDuration() - mp.getCurrentPosition());
            isTimerWorking = true;
        }

        CurrentSong.makeToast(getBaseContext(), "Resumed");
    }
    public  void PauseAudio(){
        CurrentSong.makeToast(getBaseContext(), "Paused");
        mp.pause();
        try {
            timer.cancel();
            isTimerWorking = false;
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
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
        timer.cancel();
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
        mp.start();
        playNext();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
    }
    public class MusicBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }
}
