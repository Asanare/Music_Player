package com.jmulla.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamal on 24/01/2017.
 */

public class GenericSongList extends Activity {
    ArrayList<Song> list = new ArrayList<>();
    ListView listView;
    GenericSongAdapter adapter;

    public GenericSongList(ArrayList<Song> list) {
        this.list = list;
    }

    public GenericSongList() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        Intent intent = getIntent();
        list = (ArrayList<Song>) intent.getSerializableExtra("com.jmulla.musicplayer.SONGS");
        ((TextView) findViewById(R.id.tv_listname)).setText(intent.getStringExtra("NAME"));
        listView = (ListView) findViewById(R.id.generic_song_list);
        adapter = new GenericSongAdapter(getBaseContext(), this.list);
        listView.setAdapter(adapter);

    }


    class GenericSongAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;
        private ArrayList<Song> songs;

        GenericSongAdapter(Context context, ArrayList<Song> songs) {
            this.songs = songs;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            View rowView = convertView;
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.generic_song_item, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }

            holder.mTitle = (TextView) rowView.findViewById(R.id.tv_title);
            holder.mTitle.setText(songs.get(position).title);
            holder.mArtist = (TextView) rowView.findViewById(R.id.tv_artist);
            holder.mArtist.setText(songs.get(position).artist);
            holder.mDuration = (TextView) rowView.findViewById(R.id.tv_duration);
            holder.mDuration.setText(Utilities.getMinutesFromMillis(Long.parseLong(songs.get(position).duration)));
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Manager.currentSongList = songs;
                    Manager.currentSong = songs.get(position);
                    //String songId = allSongs.get(position).id;
                    Intent intent = new Intent(getBaseContext(), CurrentSong.class);
                    intent.putExtra("START_POSITION", position);
                    v.getContext().startActivity(intent);
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
            TextView mTitle;
            TextView mArtist;
            TextView mDuration;
        }

    }
}
