package com.jmulla.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Jamal on 24/12/2016.
 */

class DatabaseHandler extends SQLiteOpenHelper {
    // Table names
    public static final String TABLE_SONGS = "SongData";
    public static final String TABLE_PLAYLIST = "Playlist";
    public static final String TABLE_BLACKLIST = "Blacklist";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MainDatabase";
    // Contacts Table Columns names
    private static final String KEY_SONGS_ID = "id";
    private static final String KEY_SONGS_TITLE = "title";
    private static final String KEY_SONGS_ARTIST = "artist";
    private static final String KEY_SONGS_ALBUM = "album";
    private static final String KEY_SONGS_COVER_LOC = "cover_loc";
    private static final String KEY_SONGS_DURATION = "duration";
    private static final String KEY_SONGS_LOCATION = "location";
    private static final String KEY_SONG_ID = "song_id";
    private static final String KEY_PLAYLISTS_ID = "id";
    private static final String KEY_PLAYLISTS_NAME = "name";
    private static final String KEY_PLAYLISTS_TRACKS = "tracks";
    private static final String KEY_PLAYLISTS_SONGS = "songs";
    private static final String KEY_PLAYLIST_ID = "playlist_id";
    private static final String KEY_BLACKLIST_SONGS = "songs";

    private final Context myContext;

    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONGDATA_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
                + KEY_SONGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SONGS_TITLE + " TEXT,"
                + KEY_SONGS_ARTIST + " TEXT," + KEY_SONGS_ALBUM + " TEXT," + KEY_SONGS_COVER_LOC + " TEXT," + KEY_SONGS_DURATION + " TEXT," + KEY_SONGS_LOCATION + " TEXT," + KEY_SONG_ID + " TEXT);";
        db.execSQL(CREATE_SONGDATA_TABLE);
        String CREATE_PLAYLISTDATA_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + KEY_PLAYLISTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PLAYLISTS_NAME + " TEXT,"
                + KEY_PLAYLISTS_TRACKS + " TEXT," + KEY_PLAYLISTS_SONGS + " TEXT," + KEY_PLAYLIST_ID + " TEXT);";
        db.execSQL(CREATE_PLAYLISTDATA_TABLE);
        String CREATE_BLACKLIST_TABLE = "CREATE TABLE " + TABLE_BLACKLIST + "("
                + KEY_SONGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SONGS_TITLE + " TEXT,"
                + KEY_SONGS_ARTIST + " TEXT," + KEY_SONGS_DURATION + " TEXT," + KEY_SONGS_LOCATION + " TEXT," + KEY_SONG_ID + " TEXT);";
        db.execSQL(CREATE_BLACKLIST_TABLE);
        Log.d("DB STATUS: ", "CREATED");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKLIST);
        // Create tables again
        onCreate(db);
    }

    void addPlaylist(PlaylistModel playlist, DatabaseHandler dbh) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLISTS_NAME, playlist.getName());
        values.put(KEY_PLAYLISTS_TRACKS, playlist.getNumberOfTracks());

        Gson gson = new Gson();
        String jsonSongs = gson.toJson(playlist.getSongs());
        values.put(KEY_PLAYLISTS_SONGS, jsonSongs);
        values.put(KEY_PLAYLIST_ID, playlist.getId());

        // Inserting Row
        db.insert(TABLE_PLAYLIST, null, values);
        db.close(); // Closing database connection
    }

    /*void addSongToBlacklist(Song song){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> allSongs = getBlacklistedSongs();
        for(Song song1: allSongs){
            CurrentSong.makeToast(myContext, song1.title);
            if (song1.id.equals(song.id)){
                return;
            }
        }
        allSongs.add(song);
        Gson gson = new Gson();
        String jsonSongs = gson.toJson(allSongs);
        ContentValues values = new ContentValues();
        values.put(KEY_BLACKLIST_SONGS, jsonSongs);
        //CurrentSong.makeToast(myContext, "ALLSONGS SIZE: " + String.valueOf(allSongs.size()));
        db.rawQuery("DELETE FROM "+TABLE_BLACKLIST, null);
        db.insert(TABLE_BLACKLIST, null, values);
        db.close();
    }
    ArrayList<Song> getBlacklistedSongs() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Song> songs = new ArrayList<>();
        //Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BLACKLIST, null);
        Cursor cursor = db.query(TABLE_BLACKLIST, new String[]{KEY_BLACKLIST_SONGS},
                null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        assert cursor != null;
        if(cursor.getCount() != 0){
            //CurrentSong.makeToast(myContext, "DOING SOMET");
            Type type = new TypeToken<ArrayList<Song>>() {}.getType();
            songs = new Gson().fromJson(cursor.getString(0), type);
            cursor.close();
        }
        db.close();


        // return contact
        CurrentSong.makeToast(myContext, "THE NUMBER OF BLACKLISTED SONGS IS " + String.valueOf(songs.size()));
        return songs;
    }*/
    void addSong(Song song, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> songArrayList = getAllSongs(tableName, false);
        for (Song song1 : songArrayList) {
            if (song1.id.equals(song.id)) {
                return;
            }
        }
        ContentValues values = new ContentValues();
        values.put(KEY_SONGS_TITLE, song.title);
        values.put(KEY_SONGS_ARTIST, song.artist);
        values.put(KEY_SONGS_ALBUM, song.album);
        values.put(KEY_SONGS_COVER_LOC, song.cover_loc);
        values.put(KEY_SONGS_DURATION, song.duration);
        values.put(KEY_SONGS_LOCATION, song.location);
        values.put(KEY_SONG_ID, song.id);

        // Inserting Row
        db.insert(tableName, null, values);
        db.close(); // Closing database connection
    }

    public PlaylistModel getPlaylist(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLAYLIST, new String[]{KEY_PLAYLISTS_ID,
                        KEY_PLAYLISTS_NAME, KEY_PLAYLISTS_TRACKS, KEY_PLAYLISTS_SONGS, KEY_PLAYLIST_ID}, KEY_PLAYLIST_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        ArrayList<Song> songs = new Gson().fromJson(cursor.getString(3), type);
        PlaylistModel playlistmodel = new PlaylistModel(cursor.getString(1), songs, cursor.getString(4));
        // return contact
        cursor.close();
        return playlistmodel;
    }

    // Getting single contact
    public Song getSong(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, new String[]{KEY_SONG_ID,
                        KEY_SONGS_TITLE, KEY_SONGS_ARTIST, KEY_SONGS_ALBUM, KEY_SONGS_COVER_LOC, KEY_SONGS_DURATION, KEY_SONGS_LOCATION}, KEY_SONG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        Song song = new Song(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        // return contact
        cursor.close();
        return song;
    }

    public ArrayList<PlaylistModel> getAllPlaylists() {
        ArrayList<PlaylistModel> playlists = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PLAYLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Type type = new TypeToken<ArrayList<Song>>() {
                }.getType();
                ArrayList<Song> songs = new Gson().fromJson(cursor.getString(3), type);
                PlaylistModel playlistmodel = new PlaylistModel(cursor.getString(1), songs, cursor.getString(4));
                playlists.add(playlistmodel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return playlists;
    }

    // Getting All Songs
    public ArrayList<Song> getAllSongs(String tableName, boolean close) {
        ArrayList<Song> songList = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + tableName;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(cursor.getString(7), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (close) {
            db.close();
        }
        // return contact list
        return songList;
    }


    // Getting contacts Count
    public int getCount(String tableName) {
        String countQuery = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single contact
    public int updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONGS_TITLE, song.title);
        values.put(KEY_SONGS_ARTIST, song.artist);
        values.put(KEY_SONGS_ALBUM, song.album);
        values.put(KEY_SONGS_COVER_LOC, song.cover_loc);
        values.put(KEY_SONGS_DURATION, song.duration);
        values.put(KEY_SONGS_LOCATION, song.location);
        values.put(KEY_SONG_ID, song.id);

        // updating row
        return db.update(TABLE_SONGS, values, KEY_SONG_ID + " = ?",
                new String[]{String.valueOf(song.id)});
    }

    public int updatePlaylist(PlaylistModel playlist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLISTS_NAME, playlist.getName());
        values.put(KEY_PLAYLISTS_TRACKS, playlist.getNumberOfTracks());
        Gson gson = new Gson();
        String jsonSongs = gson.toJson(playlist.getSongs());
        values.put(KEY_PLAYLISTS_SONGS, jsonSongs);
        values.put(KEY_PLAYLIST_ID, playlist.getId());
        // updating row
        return db.update(TABLE_PLAYLIST, values, KEY_PLAYLISTS_ID + " = ?",
                new String[]{});
    }

    public void addSongToPlaylist(PlaylistModel playlist, Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Song> songArrayList = getPlaylist(playlist.getId()).getSongs();
        for (Song loopSong : songArrayList) {
            if (loopSong.id.equals(song.id)) {
                return;
            }
        }
        songArrayList.add(song);
        Gson gson = new Gson();
        String jsonSongs = gson.toJson(songArrayList);
        // updating row
        Cursor c = db.rawQuery("UPDATE " + TABLE_PLAYLIST + " SET " + KEY_PLAYLISTS_SONGS + " = " + "'" + jsonSongs + "'" + " WHERE " + KEY_PLAYLIST_ID + " = " + "'" + playlist.getId() + "'", null);
        CurrentSong.makeToast(myContext, c.getCount());
        c.close();
        db.close();
    }

    // Deleting single contact
    public void deleteSong(Song song, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, KEY_SONG_ID + " = ?",
                new String[]{String.valueOf(song.id)});
        db.close();
    }

    void deletePlaylist(PlaylistModel playlistModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST, KEY_PLAYLIST_ID + " = ?",
                new String[]{String.valueOf(playlistModel.getId())});
        db.close();
    }


    public void deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE FROM " + tableName, null);
        db.close();


    }


}
