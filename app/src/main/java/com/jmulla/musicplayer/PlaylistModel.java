package com.jmulla.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jamal on 17/01/2017.
 */
//Class used to store playlist data in an organised way.
class PlaylistModel {
    //member variables
    private String mName = "";
    private ArrayList<Song> mSongs = new ArrayList<>();
    private String mId = "";

    //constructor//
    PlaylistModel(String name, ArrayList<Song> songs, String id) {
        this.mName = name;
        this.mSongs = songs;
        this.mId = id;
    }

    //setters and getters
    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getNumberOfTracks() {
        return String.valueOf(this.mSongs.size());
    }

    public ArrayList<Song> getSongs() {
        return this.mSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.mSongs = songs;
    }

    public String getId() {
        return this.mId;
    }
}
