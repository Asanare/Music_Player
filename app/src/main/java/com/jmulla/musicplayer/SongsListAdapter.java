package com.jmulla.musicplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

/**
 * Created by Jamal on 15/07/2016.
 */
public class SongsListAdapter extends DragItemAdapter<Pair<Long, Song>, SongsListAdapter.ViewHolder> implements Filterable {

    public ArrayList<Integer> selectedIds = new ArrayList<>();
    ArrayList<Song> allSongs = new ArrayList<>();
    View view;
    ArrayList<ArtistModel> allArtists;
    ArrayList<AlbumModel> allAlbums;
    Context mContext;
    private int mLayoutId;
    private int mGrabHandleId;
    private String selectedId = "";

    public SongsListAdapter(ArrayList<Pair<Long, Song>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, final Context context) {
        super(dragOnLongPress);

        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                allArtists = new Manager().getAllArtistData(context.getApplicationContext());
                allAlbums = new Manager().getAllAlbumData(context.getApplicationContext());
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        mContext = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
/*        if (holder.itemView.getTag(getItemId(position))) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.app_color));
        }*/
        ///holder.setIsRecyclable(true);
        holder.mTitle.setText(mItemList.get(position).second.title);
        holder.mArtist.setText(mItemList.get(position).second.artist);
        holder.mDuration.setText(Utilities.getMinutesFromMillis(Long.parseLong(mItemList.get(position).second.duration)));
        //holder.itemView.setTag(mItemList.get(position).second.title);
    }


    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Pair<String, ArrayList<Object>>> results = new ArrayList<>();
                ArrayList<ArtistModel> artists = new ArrayList<>();
                ArrayList<AlbumModel> albums = new ArrayList<>();
                results.add(new Pair<>("Title", new ArrayList<>()));
                results.add(new Pair<>("Artist", new ArrayList<>()));
                results.add(new Pair<>("Albums", new ArrayList<>()));
                final ArrayList<Song> original = new ArrayList<>();
                for (Pair<Long, Song> song : mItemList) {
                    original.add(song.second);
                }
                if (constraint != null) {
                    if (original.size() > 0) {
                        for (final Song s : original) {
                            if (s.title.toUpperCase().contains(constraint.toString().toUpperCase())) {
                                results.get(0).second.add(s);//Add to title section if title matches constraint
                            } /*else if (s.artist.toUpperCase().contains(constraint.toString().toUpperCase())) {
                                if (!artists.contains(s.artist)) {
                                    artists.add(s.artist);
                                    //Add to artist section if artist matches constraint
                                }
                            }
                            else if(s.album.toUpperCase().contains(constraint.toString().toUpperCase())){
                                if (!albums.contains(new Pair<>(s.album, s.artist))) {
                                    albums.add(new Pair<>(s.album, s.artist));
                                }
                            }*/
                        }
                        for (ArtistModel artistModel : allArtists) {
                            if (artistModel.getArtist().toUpperCase().contains(constraint.toString().toUpperCase())) {
                                if (!artists.contains(artistModel)) {
                                    artists.add(artistModel);
                                }
                            }
                        }
                        for (AlbumModel albumModel : allAlbums) {
                            if (albumModel.getAlbum().toUpperCase().contains(constraint.toString().toUpperCase())) {
                                if (!albums.contains(albumModel)) {
                                    albums.add(albumModel);
                                }
                            }
                        }
                    }
                    results.get(1).second.addAll(artists);
                    results.get(2).second.addAll(albums);
/*
                    for (String artist : artists) {
                        results.get(1).second.add(new ArtistModel(artist));
                    }
                    for (Pair<String, String> data : albums) {
                        results.get(2).second.add(new AlbumModel(data.first, data.second));
                    }*/
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Listfragment.initSearchResults((ArrayList<Pair<String, ArrayList<Object>>>) results.values, view, mContext);
            }
        };
    }

    private void createDialog(String title, String message, String positive, String negative, boolean cancelable, final Runnable func) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setCancelable(cancelable);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                func.run();
            }
        })

                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    class ViewHolder extends DragItemAdapter<Pair<Long, Song>, SongsListAdapter.ViewHolder>.ViewHolder {

        final ActionMode.Callback mActionModeCallback;
        TextView mTitle;
        TextView mArtist;
        TextView mDuration;
        ActionMode mActionMode;


        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mArtist = (TextView) itemView.findViewById(R.id.tv_artist);
            mDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            //itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.app_color));
            //Log.d("ID", Integer.toString(mArtist.getId()));
/*try{

    if(itemView.getTag(itemView.getId()).toString().equals("S")){
        itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.app_color));
    }else{
        itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark));
    }
}catch (Exception e){
    e.printStackTrace();
}finally {
    itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark));
}*/



            mActionModeCallback = new ActionMode.Callback() {

                // Called when the action mode is created; startActionMode() was called
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate a menu resource providing context menu items
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.song_cab, menu);
                    return true;
                }

                // Called each time the action mode is shown. Always called after onCreateActionMode, but
                // may be called multiple times if the mode is invalidated.
                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false; // Return false if nothing is done
                }

                // Called when the user selects a contextual menu item
                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.song_rename:
                            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                            final EditText edittext = new EditText(mContext);
                            alert.setMessage("Rename song locally");
                            alert.setTitle("Rename");
                            alert.setView(edittext);
                            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String renamedValue = edittext.getText().toString();
                                    Song song = mItemList.get(getLayoutPosition()).second;
                                    song.title = renamedValue;
                                    new DatabaseHandler(mContext).updateSong(song);
                                    mItemList.remove(getLayoutPosition());
                                    mItemList.add(getLayoutPosition(), new Pair<>((long) getLayoutPosition(), song));
                                    Listfragment.listAdapter.notifyDataSetChanged();
                                    Listfragment.sortAlphabetical();
                                    mode.finish();
                                }
                            });
                            alert.show();
                            return true;
                        case R.id.song_edit_tags:
                            Intent intent = new Intent(mContext, TagEditor.class);
                            intent.putExtra("com.jmulla.musicplayer.TagEditor.SONG", mItemList.get(getLayoutPosition()).second);
                            intent.putExtra("com.jmulla.musicplayer.TagEditor.POSITION", getLayoutPosition());
                            mContext.startActivity(intent);
                            mode.finish();
                            return true;
                        case R.id.song_top_queue:
                            changeItemPosition(getLayoutPosition(), 0);
                            mode.finish();
                            return true;
                        case R.id.song_bottom_queue:
                            changeItemPosition(getLayoutPosition(), mItemList.size() - 1);
                            mode.finish();
                            return true;
                        case R.id.song_blacklist:
                            Runnable blacklistRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    DatabaseHandler dbh = new DatabaseHandler(mContext);
                                    dbh.addSong(mItemList.get(getLayoutPosition()).second, DatabaseHandler.TABLE_BLACKLIST);
                                    dbh.deleteSong(mItemList.get(getLayoutPosition()).second, DatabaseHandler.TABLE_SONGS);
                                    mItemList.remove(getLayoutPosition());
                                    Listfragment.listAdapter.notifyDataSetChanged();
                                    mode.finish();
                                }
                            };
                            createDialog("Blacklist", "Do you want to blacklist this song?", "Yes", "Cancel", false, blacklistRunnable);
                            return true;
                        case R.id.song_delete:
                            Runnable deleteRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    DatabaseHandler db = new DatabaseHandler(mContext);
                                    db.deleteSong(mItemList.get(getLayoutPosition()).second, DatabaseHandler.TABLE_SONGS);
                                    mItemList.remove(getLayoutPosition());
                                    Listfragment.listAdapter.notifyDataSetChanged();
                                }
                            };
                            createDialog("Delete", "Do you want to delete this song permanently?", "Delete", "Cancel", false, deleteRunnable);
                            return true;
                        case R.id.song_add_to_playlist:
                            final DatabaseHandler handler = new DatabaseHandler(mContext);
                            final ArrayList<PlaylistModel> allPlaylists = handler.getAllPlaylists();
                            CharSequence[] names;
                            if (allPlaylists.size() == 0) {
                                Toast.makeText(mContext, "No playlists yet. Go make one", Toast.LENGTH_SHORT).show();
                            } else {
                                names = new CharSequence[allPlaylists.size()];
                                for (int i = 0; i < allPlaylists.size(); i++) {
                                    names[i] = allPlaylists.get(i).getName();
                                }

                                new AlertDialog.Builder(mContext)
                                        .setSingleChoiceItems(names, 0, null)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                                handler.addSongToPlaylist(allPlaylists.get(selectedPosition), mItemList.get(getLayoutPosition()).second);
/*                                            PlaylistsTab.playlistModels = new Manager().getPlaylists(context);
                                            PlaylistsTab.adapter.notifyDataSetChanged();*/

                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                            }
                            mode.finish();
                            return true;
                        default:
                            return false;

                    }
                }

                // Called when the user exits the action mode
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mActionMode = null;
                }
            };

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
            intent.putExtra("START_POSITION", position);
            /*Bundle b = new Bundle();
            b.putSerializable("Songs", allSongs);
            intent.putExtras(b);*/
            /*intent.putExtra("SONG_LOCATION", allSongs.get(position).location);
            intent.putExtra("SONG_ARTIST", allSongs.get(position).artist);
            intent.putExtra("SONG_DURATION", allSongs.get(position).duration);
            intent.putExtra("SONG_TITLE", allSongs.get(position).title);*/

            //intent.putExtra("SONG_ID", songId);

            view.getContext().startActivity(intent);

            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }


        @Override
        public boolean onItemLongClicked(View view) {
            int pos = this.getLayoutPosition();
            CurrentSong.makeToast(view.getContext(), pos);
            if (mActionMode != null) {
                return false;
            }

            // Start the CAB using the ActionMode.Callback defined above
            //mActionMode = getActivity().startActionMode(mActionModeCallback);
            ActionBar actionBar = ((MainActivity) view.getContext()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.startActionMode(mActionModeCallback);
            }
            //mActionMode = ((MainActivity) view.getContext()).startActionMode(mActionModeCallback);

            view.setSelected(true);

/*            selectedIds.add(view.getId());
            if(selectedIds.contains(pos)){
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.app_color));
            }
            else {
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            }*/
/*            CurrentSong.makeToast(context, mItemList.get(getLayoutPosition()).second.getLocation());*/
            return true;


/*            if (selectedId.equals(mItemList.get(pos).first.toString())) {
                selectedId = "";
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            } else {
                selectedId = mItemList.get(pos).first.toString();
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.app_color));
            }*/


            /*if(selectedIds.contains(pos)){
                //view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.app_color));
                selectedIds.remove(pos);
            }
            else {
                selectedIds.add(pos);
                //view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
            }*/


        }


    }




}
