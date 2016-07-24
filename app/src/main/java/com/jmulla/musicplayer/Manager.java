package com.jmulla.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Jamal on 14/07/2016.
 */
public class Manager extends Activity {
    static MediaPlayer mp = new MediaPlayer();

    public ArrayList<ArrayList<String>> getAllMusicData(Context context, int type) {
        Cursor cursor;
        //ArrayList<String> ids = new ArrayList<>(), artists = new ArrayList<>(), titles= new ArrayList<>(), paths= new ArrayList<>(), display_names= new ArrayList<>(), durations_ms= new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

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
                null);

        ArrayList<ArrayList<String>> songs = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                /*ids.add(cursor.getString(0));
                artists.add(cursor.getString(1));
                titles.add(cursor.getString(2));
                paths.add(cursor.getString(3));
                display_names.add(cursor.getString(4));
                durations_ms.add(cursor.getString(5));*/
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
        if (cursor != null) {
            cursor.close();
        }
        switch (type) {
            case 0:
                return songs;
/*            case 1:
                return ids;
            case 2:
                return artists;
            case 3:
                return titles;
            case 4:
                return paths;
            case 5:
                return display_names;
            case 6:
                return durations_ms;*/
            default:
                return songs;
        }
    }
}
