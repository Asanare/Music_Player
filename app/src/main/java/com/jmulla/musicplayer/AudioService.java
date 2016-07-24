package com.jmulla.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.util.ArrayList;

public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    private MediaPlayer mp;
    private ArrayList<Song> songs;
    private int songPosition;
    private IBinder iBinder = new MusicBinder();

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

    public void PlayAudio(String location){

        try {
            mp.reset();
            mp.setDataSource(location);
            mp.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void ResumeAudio(){
        mp.start();
    }
    public  void PauseAudio(){
        mp.pause();
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
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
    public class MusicBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }
}
