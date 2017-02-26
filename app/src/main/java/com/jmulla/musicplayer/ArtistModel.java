package com.jmulla.musicplayer;

import java.util.ArrayList;

/***
 * Created by Jamal on 16/01/2017.
 */
//holds all the information about an artist including their songs and name
public class ArtistModel {
    //member variables//
    private String mArtist = ""; //artist name
    private ArrayList<Song> mSongs; //all the songs that share this artist

    //construtor//
    public ArtistModel(String artist) {
        //initialises this class
        mArtist = artist;
        mSongs = new ArrayList<>();
    }

    //setters and getters//
    public void addSong(Song songs) {
        this.mSongs.add(songs);
    }  //add song to the list

    public String getArtist() {
        return this.mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public int getNumberOfTracks() {
        return this.mSongs.size();
    }

    public ArrayList<Song> getSongs() {
        return this.mSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.mSongs = songs; //replace song list with a new one
    }

}
