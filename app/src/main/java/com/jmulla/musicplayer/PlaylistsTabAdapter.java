/*
package com.jmulla.musicplayer;

*/
/**
 * Created by Jamal on 17/01/2017.
 *//*


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


class PlaylistsTabAdapter extends BaseAdapter {
    private ArrayList<PlaylistModel> mPlaylistModels;
    private Context mContext;
    private static LayoutInflater inflater=null;
    PlaylistsTabAdapter(Context context, ArrayList<PlaylistModel> playlistModels) {
        // TODO Auto-generated constructor stub
        mPlaylistModels=playlistModels;
        mContext=context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mPlaylistModels.size();
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
        TextView p_name;
        TextView p_tracks;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder;
        View rowView = convertView;
        if(rowView == null){
            rowView = inflater.inflate(R.layout.playlists_list, parent, false);
            holder = new Holder();
            rowView.setTag(holder);
        }
        else {
            holder = (Holder) rowView.getTag();
        }

        holder.p_name=(TextView) rowView.findViewById(R.id.playlist_name);
        holder.p_name.setText(mPlaylistModels.get(position).getName());
        holder.p_tracks = (TextView)  rowView.findViewById(R.id.playlist_tracks);
        holder.p_tracks.setText(mPlaylistModels.get(position).getNumberOfTracks());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(mContext, "You Clicked "+mPlaylistModels.get(position).getName(), Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

}
*/
