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

/***
 * Created by Jamal on 13/07/2016.
 */
//this class is another tab which the user can navigate to. This one shows all the artists
public class ArtistsTab extends Fragment {
    //member variables//
    ArtistsTabAdapter adapter; //adapter which is used to display the data in a listview
    ListView lv; //listview which will show all the artist data
    Manager manager = new Manager();

    //method which returns a new instance of this class
    public static ArtistsTab newInstance() {
        return new ArtistsTab();
    }

    //method used to create the view and initialise the tab
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.artists_tab, container, false);
        init(view);
        return view;
    }

    //method that initialises the tab with default values and populates the listview
    void init(View view) {
        lv = (ListView) view.findViewById(R.id.lv_artists);
        ArrayList<ArtistModel> artists = manager.getAllArtistData(getContext());  //gets all the artist data
        adapter = new ArtistsTabAdapter(getContext(), artists); //creates a new adapter and gives it the list we want showing
        lv.setAdapter(adapter);

    }


    //this is the adapter for the artist data. It takes the data, processes it and shows it to the user in a listview
    class ArtistsTabAdapter extends BaseAdapter {
        private ArrayList<ArtistModel> mArtists;
        private Context mContext;
        private LayoutInflater inflater = null;

        //constructor//
        ArtistsTabAdapter(Context context, ArrayList<ArtistModel> artists) {
            mArtists = artists;
            mContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //getters//
        @Override
        public int getCount() {
            return mArtists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //method that creates and returns the view for each artist in the listview
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            View rowView = convertView;
            //if the row is null, then inflate it
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.artists_list, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }
            //set the correct values by using the artist data
            holder.artistName = (TextView) rowView.findViewById(R.id.artist_name);
            holder.artistName.setText(mArtists.get(position).getArtist());
            holder.tracks = (TextView) rowView.findViewById(R.id.artist_tracks);
            holder.tracks.setText(String.format("Songs: %s", String.valueOf(mArtists.get(position).getNumberOfTracks())));
            //if the row is clicked, open up a new list with all the songs that share this artist
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GenericSongList.class);
                    intent.putExtra("com.jmulla.musicplayer.SONGS", mArtists.get(position).getSongs());
                    intent.putExtra("NAME", mArtists.get(position).getArtist());
                    startActivity(intent); //start the new activity with all the songs
                }
            });
            return rowView;
        }

        //holder to hold the 2 textviews which are inflated in the getView method
        private class Holder {
            TextView artistName;
            TextView tracks;
        }

    }
}
