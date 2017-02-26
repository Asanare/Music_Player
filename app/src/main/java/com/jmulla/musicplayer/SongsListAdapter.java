package com.jmulla.musicplayer;
/***
 * Created by Jamal on 15/07/2016.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
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

//Adapter to show all the songs in a queue. This is the main adapter used in the system
class SongsListAdapter extends DragItemAdapter<Pair<Long, Song>, SongsListAdapter.ViewHolder> implements Filterable {
    View view;
    //member variables//
    private ArrayList<Song> allSongs = new ArrayList<>();
    private ArrayList<ArtistModel> allArtists;
    private ArrayList<AlbumModel> allAlbums;
    private Context mContext;
    private int tabColor1;
    private int tabColor2;
    private int mLayoutId;
    private int mGrabHandleId;

    //constructor//
    SongsListAdapter(ArrayList<Pair<Long, Song>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, final Context context) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.ViewStyle);
        tabColor1 = ta.getColor(R.styleable.ViewStyle_colorBar1, 0);
        tabColor2 = ta.getColor(R.styleable.ViewStyle_colorBar2, 0);
        //get the artist and album data in the background so the UI does not freeze
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                allArtists = new Manager().getAllArtistData(context.getApplicationContext());
                allAlbums = new Manager().getAllAlbumData(context.getApplicationContext());
            }
        });
    }

    //Create the viewholder and inflate the layout
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    //Set the correct information when the view is bound
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.mTitle.setText(mItemList.get(position).second.title);
        holder.mArtist.setText(mItemList.get(position).second.artist);
        holder.mDuration.setText(Utilities.getMinutesFromMillis(Long.parseLong(mItemList.get(position).second.duration)));
        if (holder.getAdapterPosition() % 2 == 0) {
            holder.itemView.setBackgroundColor(tabColor1);
        } else {
            holder.itemView.setBackgroundColor(tabColor2);
        }
    }


    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    //this is where the filtering of the songs, artists and albums happens. The constraint is checked against all the songs, artists and albums
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Pair<String, ArrayList<Object>>> results = new ArrayList<>();
                ArrayList<ArtistModel> artists = new ArrayList<>();
                ArrayList<AlbumModel> albums = new ArrayList<>();
                //create 3 sections for songs, artists and albums
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
                            }
                        }
                        for (ArtistModel artistModel : allArtists) {
                            if (artistModel.getArtist().toUpperCase().contains(constraint.toString().toUpperCase())) {
                                if (!artists.contains(artistModel)) {
                                    artists.add(artistModel);  //add to the artists list if the artist matches constraint
                                }
                            }
                        }
                        for (AlbumModel albumModel : allAlbums) {
                            if (albumModel.getAlbum().toUpperCase().contains(constraint.toString().toUpperCase())) {
                                if (!albums.contains(albumModel)) {
                                    albums.add(albumModel);  //add to the albums list if the album matches constraint
                                }
                            }
                        }
                    }
                    results.get(1).second.addAll(artists);
                    results.get(2).second.addAll(albums);
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //return results of filtering
                Listfragment.initSearchResults((ArrayList<Pair<String, ArrayList<Object>>>) results.values, view, mContext);
            }
        };
    }

    //generic dialog method
    private void createDialog(String title, String message, String positive, String negative, boolean cancelable, final Runnable func) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setCancelable(cancelable);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //run the function
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

    //holder for the song details
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

            //actionmode for the conetxt options
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
                            //dialog for renaming a song
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
                            //opens another activity so the user can edit the ID3 tags
                            Intent intent = new Intent(mContext, TagEditor.class);
                            intent.putExtra("com.jmulla.musicplayer.TagEditor.SONG", mItemList.get(getLayoutPosition()).second);
                            intent.putExtra("com.jmulla.musicplayer.TagEditor.POSITION", getLayoutPosition());
                            mContext.startActivity(intent);
                            mode.finish();
                            return true;
                        case R.id.song_top_queue:
                            //moves this song to the top of the queue
                            changeItemPosition(getLayoutPosition(), 0);
                            notifyDataSetChanged();
                            mode.finish();
                            return true;
                        case R.id.song_bottom_queue:
                            //moves this song to the bottom of the queue
                            changeItemPosition(getLayoutPosition(), mItemList.size() - 1);
                            notifyDataSetChanged();
                            mode.finish();
                            return true;
                        case R.id.song_blacklist:
                            //blacklists this song and removes it fromm the current list
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
                            //deletes this song permanently
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
                            //adds the current song to a playlist. If there isn't a playlist, a message is shown saying that. If there are, the user can choose one
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

        //Plays the songs that was clicked
        @Override
        public void onItemClicked(View view) {
            Integer position = getLayoutPosition();
            allSongs.clear();
            for (Pair<Long, Song> song : mItemList) {
                allSongs.add(song.second);
            }
            Manager.currentSongList = allSongs;
            Manager.currentSong = allSongs.get(position);
            Intent intent = new Intent(view.getContext(), CurrentSong.class);
            intent.putExtra("START_POSITION", position);
            view.getContext().startActivity(intent);
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        //brings up the context options when long clicked
        @Override
        public boolean onItemLongClicked(View view) {
            int pos = this.getLayoutPosition();
            CurrentSong.makeToast(view.getContext(), pos);
            if (mActionMode != null) {
                return false;
            }

            // Start the CAB using the ActionMode.Callback defined above
            ActionBar actionBar = ((MainActivity) view.getContext()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.startActionMode(mActionModeCallback);
            }
            return true;
        }


    }


}
