package com.jmulla.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jamal on 16/01/2017.
 */

public class AlbumModel {

    private String mArtist = "";
    private String mTracks = "";
    private ArrayList<Song> mSongs;

    public AlbumModel(String artist) {
        mArtist = artist;
        mSongs = new ArrayList<>();
    }

    public void addSong(Song songs) {
        this.mSongs.add(songs);
    }

    /***********
     * Get Methods
     ****************/

    public String getArtist() {
        return this.mArtist;
    }

    /***********
     * Set Methods
     ******************/

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getTracks() {
        return this.mTracks;
    }

    public void setTracks(String tracks) {
        this.mTracks = tracks;
    }

    public int getNumberOfTracks() {
        return this.mSongs.size();
    }

    public ArrayList<Song> getSongs() {
        return this.mSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.mSongs = songs;
    }

}