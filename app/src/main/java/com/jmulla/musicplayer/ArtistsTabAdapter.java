/*
package com.jmulla.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class ArtistsTabAdapter extends BaseAdapter{
    private ArrayList<ArtistModel> mArtists;
    private Context mContext;
    private static LayoutInflater inflater=null;
    ArtistsTabAdapter(Context context, ArrayList<ArtistModel> artists) {
        // TODO Auto-generated constructor stub
        mArtists=artists;
        mContext=context;
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

    private class Holder
    {
        TextView artistName;
        TextView tracks;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder;
        View rowView = convertView;
        if(rowView == null){
            rowView = inflater.inflate(R.layout.artists_list, parent, false);
            holder = new Holder();
            rowView.setTag(holder);
        }
        else {
            holder = (Holder) rowView.getTag();
        }

        holder.artistName=(TextView) rowView.findViewById(R.id.artist_name);
        holder.artistName.setText(mArtists.get(position).getArtist());
        holder.tracks = (TextView) rowView.findViewById(R.id.artist_tracks);
        holder.tracks.setText(String.valueOf(mArtists.get(position).getNumberOfTracks()));
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "You Clicked "+mArtists.get(position), Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

} */
