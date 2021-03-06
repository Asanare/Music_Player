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

/***
 * Created by Jamal on 24/01/2017.
 */
//Class used to display a list of some items. Any list of songs can be displayed. Used for albums and artists
public class GenericSongList extends Activity {
    ArrayList<Song> list = new ArrayList<>();
    ListView listView;
    GenericSongAdapter adapter;

    //constructors
    public GenericSongList(ArrayList<Song> list) {
        this.list = list;
    }

    public GenericSongList() {

    }

    //method called when the class is created. Initialises the listview
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setThemeHere(this); //sets the theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        Intent intent = getIntent();
        list = (ArrayList<Song>) intent.getSerializableExtra("com.jmulla.musicplayer.SONGS");
        ((TextView) findViewById(R.id.tv_listname)).setText(intent.getStringExtra("NAME"));
        listView = (ListView) findViewById(R.id.generic_song_list);
        adapter = new GenericSongAdapter(getBaseContext(), this.list);
        listView.setAdapter(adapter);
    }

    //Generic adapter used to show a list of songs
    class GenericSongAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;
        private ArrayList<Song> songs;

        //constructor
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
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //method that inflates the views and lets the user see the songs in the list
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
            //set the correct details
            holder.mTitle = (TextView) rowView.findViewById(R.id.tv_title);
            holder.mTitle.setText(songs.get(position).title);
            holder.mArtist = (TextView) rowView.findViewById(R.id.tv_artist);
            holder.mArtist.setText(songs.get(position).artist);
            holder.mDuration = (TextView) rowView.findViewById(R.id.tv_duration);
            holder.mDuration.setText(Utilities.getMinutesFromMillis(Long.parseLong(songs.get(position).duration)));
            //set a listener so that when an item is clicked, the song is played
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Manager.currentSongList = songs;
                    Manager.currentSong = songs.get(position);
                    Intent intent = new Intent(getBaseContext(), CurrentSong.class);
                    intent.putExtra("START_POSITION", position);
                    v.getContext().startActivity(intent);
                }
            });
            return rowView;
        }

        //holder for the song details
        private class Holder {
            TextView mTitle;
            TextView mArtist;
            TextView mDuration;
        }

    }
}
