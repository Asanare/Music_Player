package com.jmulla.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jamal on 17/01/2017.
 */


public class PlaylistModel {


    private String mName = "";
    private ArrayList<Song> mSongs = new ArrayList<>();
    private String mId = "";

    PlaylistModel(String name, ArrayList<Song> songs, String id) {
        this.mName = name;
        this.mSongs = songs;
        this.mId = id;
    }

    /***********
     * Get Methods
     ****************/

    public String getName() {
        return this.mName;
    }

    /***********
     * Set Methods
     ******************/

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
