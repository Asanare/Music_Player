package com.jmulla.musicplayer;

import java.io.Serializable;

/**
 * Created by Jamal on 15/07/2016.
 */
public class Song implements Serializable {
    public String title;
    public String artist;
    public String album;
    public String cover_loc;
    public String duration;
    public String location;
    public String id;

    public Song(String id, String title, String artist, String album, String duration, String location) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover_loc = cover_loc;
        this.duration = duration;
        this.location = location;
        this.id = id;
    }

    public Song(String id, String title, String artist, String album, String cover_loc, String duration, String location) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover_loc = cover_loc;
        this.duration = duration;
        this.location = location;
        this.id = id;
    }

    public Song() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getCover_loc() {
        return cover_loc;
    }

    public void setCover_loc(String cover_loc) {
        this.cover_loc = cover_loc;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
