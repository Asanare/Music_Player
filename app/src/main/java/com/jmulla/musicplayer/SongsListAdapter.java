package com.jmulla.musicplayer;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

/**
 * Created by Jamal on 15/07/2016.
 */
public class SongsListAdapter extends DragItemAdapter<Pair<Long, Song>, SongsListAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    public ArrayList<String> selectedIds = new ArrayList<>();
    public ArrayList<Song> allSongs = new ArrayList<>();

    public SongsListAdapter(ArrayList<Pair<Long, Song>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        super(dragOnLongPress);

        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.setIsRecyclable(false);
        if (selectedIds.contains(mItemList.get(position).second.id)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.app_color));
        }
        holder.mTitle.setText(mItemList.get(position).second.title);
        holder.mArtist.setText(mItemList.get(position).second.artist);
        holder.mDuration.setText(mItemList.get(position).second.duration);
        //holder.itemView.setTag(mItemList.get(position).second.title);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }


    public class ViewHolder extends DragItemAdapter<Pair<Long, Song>, SongsListAdapter.ViewHolder>.ViewHolder {

        public TextView mTitle;
        public TextView mArtist;
        public TextView mDuration;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mArtist = (TextView) itemView.findViewById(R.id.tv_artist);
            mDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            //Log.d("ID", Integer.toString(mArtist.getId()));


            /*if (selectedIds.contains(itemView.getId())){
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.app_color));
            }
            else {

                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark));
            }*/


        }

        @Override
        public void onItemClicked(View view) {
            Integer position = getLayoutPosition();
            allSongs.clear();
            for (Pair<Long, Song> song : mItemList) {
                allSongs.add(song.second);
            }
            Manager.currentSongList = allSongs;
            Manager.currentSong = allSongs.get(position);
            //String songId = allSongs.get(position).id;
            Intent intent = new Intent(view.getContext(), CurrentSong.class);
            /*Bundle b = new Bundle();
            b.putSerializable("Songs", allSongs);
            intent.putExtras(b);*/
            /*intent.putExtra("SONG_LOCATION", allSongs.get(position).location);
            intent.putExtra("SONG_ARTIST", allSongs.get(position).artist);
            intent.putExtra("SONG_DURATION", allSongs.get(position).duration);
            intent.putExtra("SONG_TITLE", allSongs.get(position).title);*/
            intent.putExtra("START_POSITION", position);
            //intent.putExtra("SONG_ID", songId);

            view.getContext().startActivity(intent);

            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }


        @Override
        public boolean onItemLongClicked(View view) {
            int pos = this.getLayoutPosition();
            CurrentSong.makeToast(view.getContext(), pos);
            if (selectedIds.contains(mItemList.get(pos).second.id)) {
                selectedIds.remove(mItemList.get(pos).second.id);
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            } else {
                selectedIds.add((mItemList.get(pos).second.id));
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.app_color));
            }


            /*if(selectedIds.contains(pos)){
                //view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.app_color));
                selectedIds.remove(pos);
            }
            else {
                selectedIds.add(pos);
                //view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            }
            if(selectedIds.contains(pos)){
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.app_color));
            }
            else {
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            }*/
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

    }


    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Song song = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.songs_list_item, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
        if (selectedIds.contains(position)){
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }else {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }
        tvTitle.setText(song.title);
        tvArtist.setText(song.artist);
        tvDuration.setText(Utilities.getDurationBreakdown(Integer.parseInt(song.duration)));
        return convertView;
    }*/


}
