package com.jmulla.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamal on 06/01/2017.
 */

public class SearchAdapter extends BaseAdapter {
    private static final int TYPE_DIVIDER = 0;
    private static final int TYPE_SONG = 1;
    private static final int TYPE_ARTIST = 2;
    private ArrayList<Object> resultsArray;
    private LayoutInflater inflater;

    public SearchAdapter(Context context, ArrayList<Object> results) {
        this.resultsArray = results;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return resultsArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return resultsArray.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Song) {
            return TYPE_SONG;
        } else if (getItem(position) instanceof ArtistModel) {
            return TYPE_ARTIST;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return !(getItemViewType(position) == TYPE_DIVIDER);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_SONG:
                    convertView = inflater.inflate(R.layout.list_item, parent, false);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<Song> s = new ArrayList<Song>();
                            s.add((Song) getItem(position));
                            Manager.currentSongList = new ArrayList<Song>(s);
                            Manager.currentSong = (Song) getItem(position);
                            Intent intent = new Intent(v.getContext(), CurrentSong.class);
                            intent.putExtra("START_POSITION", 0);
                            v.getContext().startActivity(intent);
                        }
                    });
                    break;
                case TYPE_ARTIST:
                    convertView = inflater.inflate(R.layout.artists_list, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.search_row_header, parent, false);
                    break;

            }
        }

        switch (type) {
            case TYPE_SONG:
                Song s = (Song) getItem(position);
                TextView title = (TextView) convertView.findViewById(R.id.tv_search_title);
                TextView artist = (TextView) convertView.findViewById(R.id.tv_search_artist);
                TextView duration = (TextView) convertView.findViewById(R.id.tv_search_duration);
                title.setText(s.title);
                artist.setText(s.artist);
                duration.setText(s.duration);
                break;
            case TYPE_ARTIST:
                ArtistModel a = (ArtistModel) getItem(position);
                TextView a_name = (TextView) convertView.findViewById(R.id.artist_name);
                TextView a_tracks = (TextView) convertView.findViewById(R.id.artist_tracks);
                a_name.setText(a.getArtist());
                a_tracks.setText(String.valueOf(a.getNumberOfTracks()));
                break;
            case TYPE_DIVIDER:
                TextView headerTitle = (TextView) convertView.findViewById(R.id.headerTitle);
                String titleString = (String) getItem(position);
                headerTitle.setText(titleString);
                break;
        }

        return convertView;
    }
}