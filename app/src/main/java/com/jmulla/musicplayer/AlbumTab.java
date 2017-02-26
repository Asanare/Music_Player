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
//This fragment is one of the tabs the user can navigate. It displays all the albums.
public class AlbumTab extends Fragment {
    //member variables//
    AlbumTabAdapter adapter;
    ListView lv;
    Manager manager = new Manager();

    //method which returns a new instance of this class
    public static AlbumTab newInstance() {
        return new AlbumTab();
    }


    //method which creates the tab and inflates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.album_tab, container, false);
        init(view);
        return view;
    }

    //method which initialises the tab
    void init(View view) {
        lv = (ListView) view.findViewById(R.id.lv_albums);
        ArrayList<AlbumModel> albums = manager.getAllAlbumData(getContext()); //gets all the albums from the database
        adapter = new AlbumTabAdapter(getContext(), albums);  //makes a new adapter and passes it the data we want showing
        lv.setAdapter(adapter);

    }


    //this adapter is used by the listview to display all the albums
    class AlbumTabAdapter extends BaseAdapter {
        //member variables//
        private ArrayList<AlbumModel> mAlbums;
        private Context mContext;
        private LayoutInflater inflater = null;

        //constructor//
        AlbumTabAdapter(Context context, ArrayList<AlbumModel> albums) {
            mAlbums = albums;
            mContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //setters and getters//
        @Override
        public int getCount() {
            return mAlbums.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //the holder holds the different views that are then populated for every item in the list
            Holder holder;
            View rowView = convertView;
            //inflate the view if it's null
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.album_list, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }
            //set the correct data to the correct holder
            holder.albumName = (TextView) rowView.findViewById(R.id.album_name);
            holder.albumName.setText(mAlbums.get(position).getAlbum());
            holder.artistName = (TextView) rowView.findViewById(R.id.album_artist);
            holder.artistName.setText(mAlbums.get(position).getArtist());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GenericSongList.class);
                    intent.putExtra("com.jmulla.musicplayer.SONGS", mAlbums.get(position).getSongs());
                    intent.putExtra("NAME", mAlbums.get(position).getAlbum());
                    startActivity(intent);
                }
            });
            return rowView;
        }

        //holder which holds the 2 textviews which are then inflated above
        private class Holder {
            TextView albumName;
            TextView artistName;
        }

    }
}
