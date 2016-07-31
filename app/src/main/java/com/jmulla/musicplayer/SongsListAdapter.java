package com.jmulla.musicplayer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamal on 15/07/2016.
 */
public class SongsListAdapter extends ArrayAdapter<Song> {
    public ArrayList<Integer> selectedIds = new ArrayList<>();
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
        if (selectedIds.contains(position)){
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }else {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }
        tvTitle.setText(song.title);
        tvArtist.setText(song.artist);
        tvDuration.setText(Utilities.getDurationBreakdown(Integer.parseInt(song.duration)));
        return convertView;
    }



}
