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

/**
 * Created by Jamal on 14/07/2016.
 */
public class Manager {
    public static MediaPlayer mp = new MediaPlayer();
    public static Song currentSong;
    public static AudioService audioService;
    public static ArrayList<Song> currentSongList;
    public static ArrayList<Song> fullSongList;
    public static int appBeginCode = 0;
    public static boolean needToUpdate = false;
    public static State currentState = State.NORMAL;

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public ArrayList<ArtistModel> getAllArtistData(Context context) {
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
                //allArtistData.add(new ArtistModel(cursor.getString(0)));
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

    public ArrayList<PlaylistModel> getPlaylists(Context context) {
        DatabaseHandler playlistDatabaseHandler = new DatabaseHandler(context);
        ArrayList<PlaylistModel> playlistModels;
        playlistModels = playlistDatabaseHandler.getAllPlaylists();
        return playlistModels;
    }

    public void addPlaylists(Context context, ArrayList<PlaylistModel> playlistModels) {
        DatabaseHandler playlistDatabaseHandler = new DatabaseHandler(context);
        for (PlaylistModel playlistModel : playlistModels) {
            playlistDatabaseHandler.addPlaylist(playlistModel, playlistDatabaseHandler);
        }
    }

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
            //region old code
            /*String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION

            };

            cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);*/
            //endregion
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
                /*ids.add(cursor.getString(0));
                artists.add(cursor.getString(1));
                titles.add(cursor.getString(2));
                paths.add(cursor.getString(3));
                display_names.add(cursor.getString(4));
                durations_ms.add(cursor.getString(5));
                    ArrayList<String> songData = new ArrayList<>();
                    songData.add(cursor.getString(0));
                    songData.add(cursor.getString(1));
                    songData.add(cursor.getString(2));
                    songData.add(cursor.getString(3));
                    songData.add(cursor.getString(4));
                    songData.add(cursor.getString(5));
                    songs.add(songData);
                }
            }
*/
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
/*    public void addToPlaylist(Context context, Song song, PlaylistModel playlistModel){
        ArrayList<PlaylistModel> playlistModels = getPlaylists(context);
        DatabaseHandler playlistDatabaseHandler = new DatabaseHandler(context);
        playlistDatabaseHandler.addSongToPlaylist(playlistModel, song);
    }*/

    public enum State {
        NORMAL,
        SHUFFLE,
        REPEAT_ONE
    }
}



