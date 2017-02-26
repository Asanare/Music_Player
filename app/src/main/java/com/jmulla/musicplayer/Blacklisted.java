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

/***
 * Created by Jamal on 24/01/2017.
 */
//This activity is used to show all the songs blacklisted by the user
public class Blacklisted extends AppCompatActivity {
    ArrayList<Song> list = new ArrayList<>();
    ListView listView;
    BlacklistAdapter adapter;

    //constructors//
    public Blacklisted(ArrayList<Song> list) {
        this.list = list;
    }

    public Blacklisted() {

    }

    //method called when this activity is created. Initialises the acitvity and layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setThemeHere(this);   //set the theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklisted); //set the layout
        listView = (ListView) findViewById(R.id.lv_blacklisted);
        DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());
        list = dbh.getAllSongs(DatabaseHandler.TABLE_BLACKLIST, true);  //get all the blacklisted songs
        adapter = new BlacklistAdapter(getBaseContext(), this.list);  //create an adapter and give it all the lists
        listView.setAdapter(adapter);  //set the adpater

    }

    //adapter used to show all the blacklisted songs
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
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //method which inflates each row with the correct details of the blacklisted song
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
            //if the user clicks the remove button, the song is no longer blacklisted
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
            return rowView;
        }

        //class to hold the views that will be inflated
        private class Holder {
            TextView mTitle;
            TextView mArtist;
            ImageView mRemove;
        }

    }
}
