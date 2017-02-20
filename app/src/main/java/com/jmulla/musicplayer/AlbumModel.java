package com.jmulla.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jamal on 16/01/2017.
 */

public class AlbumModel {

    private String mAlbum = "";
    private String mArtist = "";
    private ArrayList<Song> mSongs;

    public AlbumModel(String album, String artist) {
        mAlbum = album;
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

    public String getAlbum() {
        return this.mAlbum;
    }

    public void setAlbum(String album) {
        this.mArtist = album;
    }


    public ArrayList<Song> getSongs() {
        return this.mSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.mSongs = songs;
    }

}