package com.jmulla.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamal on 24/01/2017.
 */

public class Blacklisted extends AppCompatActivity {
    ArrayList<Song> list = new ArrayList<>();
    ListView listView;
    BlacklistAdapter adapter;

    public Blacklisted(ArrayList<Song> list) {
        this.list = list;
    }

    public Blacklisted() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklisted);
        listView = (ListView) findViewById(R.id.lv_blacklisted);
        DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());
        list = dbh.getAllSongs(DatabaseHandler.TABLE_BLACKLIST, true);
        adapter = new BlacklistAdapter(getBaseContext(), this.list);
        listView.setAdapter(adapter);

    }


    class BlacklistAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;
        private ArrayList<Song> songs;

        BlacklistAdapter(Context context, ArrayList<Song> songs) {
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
                rowView = inflater.inflate(R.layout.blacklisted_item, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }

            holder.mTitle = (TextView) rowView.findViewById(R.id.tv_title);
            holder.mTitle.setText(songs.get(position).title);
            holder.mArtist = (TextView) rowView.findViewById(R.id.tv_artist);
            holder.mArtist.setText(songs.get(position).artist);
            holder.mRemove = (ImageView) rowView.findViewById(R.id.remove_from_blacklist);
            holder.mRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler databaseHandler = new DatabaseHandler(getBaseContext());
                    databaseHandler.addSong(list.get(position), DatabaseHandler.TABLE_SONGS);
                    databaseHandler.deleteSong(list.get(position), DatabaseHandler.TABLE_BLACKLIST);
                    list.remove(position);
                    notifyDataSetChanged();
                    Listfragment.listAdapter.notifyDataSetChanged();
                }
            });
/*            rowView.setOnClickListener(new View.OnClickListener() {
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
            });*/
            return rowView;
        }

        private class Holder {
            TextView mTitle;
            TextView mArtist;
            ImageView mRemove;
        }

    }
}
