package com.jmulla.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.UUID;

/***
 * Created by Jamal on 14/07/2016.
 */
public class Manager {
    //public variables
    public static MediaPlayer mp = new MediaPlayer();
    public static boolean needToRecreate = false;
    public static Song currentSong;
    public static AudioService audioService;
    public static ArrayList<Song> currentSongList;
    public static ArrayList<Song> fullSongList;
    public static boolean needToUpdate = false;
    public static State currentState = State.NORMAL;

    //generates a random id
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    //gets all the albums from the database
    ArrayList<AlbumModel> getAllAlbumData(Context context) {
        ArrayList<AlbumModel> allAlbumData = new ArrayList<>();
        LinkedHashSet<ArrayList> allAlbums = new LinkedHashSet<>();
        Cursor cursor;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.DATA
        };
        cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        AudioFile f = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(0));
                try {
                    f = AudioFileIO.read(file);
                } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                    e.printStackTrace();
                }

                if (f != null) {

                    String album = f.getTag().getFirst(FieldKey.ALBUM); //get the album from the tags
                    String artist = f.getTag().getFirst(FieldKey.ARTIST); //get the artist from the tags
                    ArrayList<String> albumData = new ArrayList<>();
                    if (album.equals("")) {
                        albumData.add(0, "Unknown");
                    } else {
                        albumData.add(0, album);
                    }
                    if (artist.equals("")) {
                        albumData.add(1, "Unknown");
                    } else {
                        albumData.add(1, artist);
                    }
                    allAlbums.add(albumData);

                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        for (ArrayList albumData : allAlbums) {
            allAlbumData.add(new AlbumModel(albumData.get(0).toString(), albumData.get(1).toString()));
        }
        for (AlbumModel albumModel : allAlbumData) {
            try {
                for (Song s : this.getAllMusicData(context)) {
                    if (albumModel.getAlbum().equals(s.album)) {
                        albumModel.addSong(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return allAlbumData;
    }

    //get all the artist data
    ArrayList<ArtistModel> getAllArtistData(Context context) {
        ArrayList<ArtistModel> allArtistData = new ArrayList<>();
        LinkedHashSet<String> allArtists = new LinkedHashSet<>();
        Cursor cursor;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.DATA
        };
        cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        AudioFile f = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(0));
                try {
                    f = AudioFileIO.read(file);
                } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                    e.printStackTrace();
                }

                if (f != null) {
                    String artist = f.getTag().getFirst(FieldKey.ARTIST);
                    if (artist.equals("")) {
                        allArtists.add("Unknown");
                    } else {
                        allArtists.add(artist);
                    }

                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        for (String artist : allArtists) {
            allArtistData.add(new ArtistModel(artist));
        }
        for (ArtistModel artistModel : allArtistData) {
            try {
                for (Song s : this.getAllMusicData(context)) {
                    if (artistModel.getArtist().equals(s.artist)) {
                        artistModel.addSong(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return allArtistData;
    }

    //get all the playlists
    public ArrayList<PlaylistModel> getPlaylists(Context context) {
        DatabaseHandler playlistDatabaseHandler = new DatabaseHandler(context);
        ArrayList<PlaylistModel> playlistModels;
        playlistModels = playlistDatabaseHandler.getAllPlaylists();
        return playlistModels;
    }

    //add a new playlist to the playlist table
    public void addPlaylists(Context context, ArrayList<PlaylistModel> playlistModels) {
        DatabaseHandler playlistDatabaseHandler = new DatabaseHandler(context);
        for (PlaylistModel playlistModel : playlistModels) {
            playlistDatabaseHandler.addPlaylist(playlistModel, playlistDatabaseHandler);
        }
    }

    //get all the songs from either the database or device storage
    public ArrayList<Song> getAllMusicData(Context context) {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);

        if (databaseHandler.getAllSongs(DatabaseHandler.TABLE_SONGS, true).size() == 0 || needToUpdate) {
            Cursor cursor;
            //ArrayList<String> ids = new ArrayList<>(), artists = new ArrayList<>(), titles= new ArrayList<>(), paths= new ArrayList<>(), display_names= new ArrayList<>(), durations_ms= new ArrayList<>();
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION

            };

            cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
            ArrayList<Song> songs = new ArrayList<>();
            AudioFile f = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    String loc = cursor.getString(1);
                    String dur = cursor.getString(2);
                    File file = new File(loc);
                    try {
                        f = AudioFileIO.read(file);
                    } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                        e.printStackTrace();
                    }
                    assert f != null;
                    Tag tag = f.getTag();
                    String title = tag.getFirst(FieldKey.TITLE);
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    String album = tag.getFirst(FieldKey.ALBUM);
                    String defaultCover = "drawable://" + R.drawable.refresh_icon;
                    songs.add(new Song(id, title, artist, album, defaultCover, dur, loc));
                }
            }
            if (cursor != null) {
                cursor.close();
            }

            DatabaseHandler db = new DatabaseHandler(context);
            try {
                db.deleteTable("TABLE_SONGS");
            } catch (Exception e) {
                Log.d("Error", e.toString());
            }

            for (Song song : songs) {
                db.addSong(song, DatabaseHandler.TABLE_SONGS);
            }
            return songs;

        } else {
            DatabaseHandler db = new DatabaseHandler(context);
            return db.getAllSongs(DatabaseHandler.TABLE_SONGS, true);
        }
    }

    //enum for the different states the playback can be in
    enum State {
        NORMAL,
        SHUFFLE,
        REPEAT_ONE
    }
}



