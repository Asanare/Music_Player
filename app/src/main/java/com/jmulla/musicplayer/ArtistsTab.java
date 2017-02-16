package com.jmulla.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamal on 13/07/2016.
 */

public class ArtistsTab extends Fragment{
    ArtistsTabAdapter adapter;
    ListView lv;
    Manager manager = new Manager();

    public static ArtistsTab newInstance() {
        return new ArtistsTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.artists_tab, container, false);
        init(view);
        return view;
    }

    void init(View view) {
        lv = (ListView) view.findViewById(R.id.lv_artists);
        ArrayList<ArtistModel> artists = manager.getAllArtistData(getContext());
        adapter = new ArtistsTabAdapter(getContext(), artists);
        lv.setAdapter(adapter);

    }

    class ArtistsTabAdapter extends BaseAdapter {
        private ArrayList<ArtistModel> mArtists;
        private Context mContext;
        private LayoutInflater inflater = null;

        ArtistsTabAdapter(Context context, ArrayList<ArtistModel> artists) {
            // TODO Auto-generated constructor stub
            mArtists = artists;
            mContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mArtists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder;
            View rowView = convertView;
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.artists_list, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }

            holder.artistName = (TextView) rowView.findViewById(R.id.artist_name);
            holder.artistName.setText(mArtists.get(position).getArtist());
            holder.tracks = (TextView) rowView.findViewById(R.id.artist_tracks);
            holder.tracks.setText(String.format("Songs: %s", String.valueOf(mArtists.get(position).getNumberOfTracks())));
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GenericSongList.class);
                    intent.putExtra("com.jmulla.musicplayer.SONGS", mArtists.get(position).getSongs());
                    intent.putExtra("NAME", mArtists.get(position).getArtist());
                    startActivity(intent);
                }
            });
            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

            return rowView;
        }

        private class Holder {
            TextView artistName;
            TextView tracks;
        }

    }
}
