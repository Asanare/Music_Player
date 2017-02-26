package com.jmulla.musicplayer;

import java.util.ArrayList;

/***
 * Created by Jamal on 16/01/2017.
 */
//This class holds all the information about any album as well as the setters and getters.
class AlbumModel {
    //member variables//
    private String mAlbum = "";
    private String mArtist = "";
    private ArrayList<Song> mSongs;

    //constructor//
    AlbumModel(String album, String artist) {
        //assign parameters to member variables
        mAlbum = album;
        mArtist = artist;
        mSongs = new ArrayList<>();
    }

    //Method to add a song to the list of songs
    void addSong(Song songs) {
        this.mSongs.add(songs);
    }

    //Getters//
    public String getArtist() {
        return this.mArtist;
    }

    //Setters//
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