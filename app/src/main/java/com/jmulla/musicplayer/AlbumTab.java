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

public class AlbumTab extends Fragment {
    AlbumTabAdapter adapter;
    ListView lv;
    Manager manager = new Manager();

    public static AlbumTab newInstance() {
        return new AlbumTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.album_tab, container, false);
        init(view);
        return view;
    }

    void init(View view) {
        lv = (ListView) view.findViewById(R.id.lv_albums);
        ArrayList<AlbumModel> albums = manager.getAllAlbumData(getContext());
        adapter = new AlbumTabAdapter(getContext(), albums);
        lv.setAdapter(adapter);

    }

    class AlbumTabAdapter extends BaseAdapter {
        private ArrayList<AlbumModel> mAlbums;
        private Context mContext;
        private LayoutInflater inflater = null;

        AlbumTabAdapter(Context context, ArrayList<AlbumModel> albums) {
            // TODO Auto-generated constructor stub
            mAlbums = albums;
            mContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mAlbums.size();
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
                rowView = inflater.inflate(R.layout.album_list, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }

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
            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

            return rowView;
        }

        private class Holder {
            TextView albumName;
            TextView artistName;
        }

    }
}
