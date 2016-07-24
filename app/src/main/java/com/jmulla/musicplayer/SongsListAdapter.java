package com.jmulla.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jamal on 15/07/2016.
 */
public class SongsListAdapter extends ArrayAdapter<Song> {
    public SongsListAdapter(Context context,  ArrayList<Song> songData) {
        super(context, 0, songData);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.songs_list_item, parent, false);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
        tvTitle.setText(song.title);
        tvArtist.setText(song.artist);
        tvDuration.setText(getDurationBreakdown(Integer.parseInt(song.duration)));

        return convertView;
    }
    public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");

        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String duration = "";
        if (String.valueOf(minutes).length() == 1){
            duration += "0" + String.valueOf(minutes) + ":";
        }
        else if (String.valueOf(minutes).length() == 2){
         duration+= String.valueOf(minutes) +":"  ;
        }
        if (String.valueOf(seconds).length() == 1){
            duration += "0" + String.valueOf(seconds);
        }
        else if (String.valueOf(seconds).length() == 2){
            duration += String.valueOf(seconds);
        }
        else {
            duration = "00:00";
        }
        return String.valueOf(duration);
    }
}
