package com.jmulla.musicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/***
 * Created by Jamal on 13/07/2016.
 */
//Fragment to show, edit and delete playlists
public class PlaylistsTab extends Fragment implements FragmentLifecycle {
    //member variables
    PlaylistsTabAdapter adapter;
    ListView lv;
    ImageButton btn_newPlaylist;
    Manager manager = new Manager();
    ActionMode mActionMode;

    //method to return a new instance of this class
    public static PlaylistsTab newInstance() {
        return new PlaylistsTab();
    }

    //method that inflates the alyout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.playlists_tab, container, false);
        init(view);
        return view;
    }

    //called when this is resumed. Reloads the playlist list
    @Override
    public void onResume() {
        super.onResume();
        new ReloadPlaylists(getContext()).execute();
    }

    @Override
    public void onResumeFragment() {
        new ReloadPlaylists(getContext()).execute();
    }

    //reloads the fragment
    public void reload() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(PlaylistsTab.this).attach(PlaylistsTab.this).commit();
    }

    //initialises the variables and sets the list of playlists up
    void init(View view) {
        lv = (ListView) view.findViewById(R.id.lv_playlists);
        btn_newPlaylist = (ImageButton) view.findViewById(R.id.btn_new_playlist);
        TextView tvPlaylists = (TextView) view.findViewById(R.id.tv_no_playlists);
        adapter = new PlaylistsTabAdapter(getContext(), manager.getPlaylists(getContext()));
        if (adapter.getCount() == 0) {
            lv.setVisibility(View.INVISIBLE);
            tvPlaylists.setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.VISIBLE);
            tvPlaylists.setVisibility(View.INVISIBLE);
        }
        lv.setAdapter(adapter);
        //creates a new playlist and brings up a dialog so the user can set the name
        btn_newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getContext());
                alert.setTitle("Create new playlist");
                alert.setMessage("Name...");
                alert.setView(edittext);
                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String playlistName = edittext.getText().toString();
                        ArrayList<PlaylistModel> p = new ArrayList<PlaylistModel>();
                        ArrayList<Song> s = new ArrayList<Song>();
                        p.add(new PlaylistModel(playlistName, s, Manager.generateId()));
                        manager.addPlaylists(getContext(), p);
                        reload();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();


            }
        });
    }

    //adapter to show the playlists in a list
    class PlaylistsTabAdapter extends BaseAdapter {
        private ArrayList<PlaylistModel> mPlaylistModels;
        private Context mContext;
        private LayoutInflater inflater = null;

        //constructor//
        PlaylistsTabAdapter(Context context, ArrayList<PlaylistModel> playlistModels) {
            mPlaylistModels = playlistModels;
            mContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        //swaps the current playlist list with a new one
        public void swapItems(ArrayList<PlaylistModel> playlistModels) {
            this.mPlaylistModels = playlistModels;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPlaylistModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //method which creates the views and shows all the playlists in a list
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Holder holder;
            View rowView = convertView;
            final ActionMode.Callback mActionModeCallback;
            //action mode used for contextual menu
            mActionModeCallback = new ActionMode.Callback() {

                // Called when the action mode is created; startActionMode() was called
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate a menu resource providing context menu items
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.playlists_cab, menu);
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
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.playlist_delete:
                            DatabaseHandler db = new DatabaseHandler(getContext());
                            db.deletePlaylist(mPlaylistModels.get(position));
                            mode.finish();
                            reload();// Action picked, so close the CAB
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
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.playlists_list, parent, false);
                holder = new Holder();
                rowView.setTag(holder);
            } else {
                holder = (Holder) rowView.getTag();
            }
            //sets the different views to the correct data
            holder.p_name = (TextView) rowView.findViewById(R.id.playlist_name);
            holder.p_name.setText(mPlaylistModels.get(position).getName());
            holder.p_tracks = (TextView) rowView.findViewById(R.id.playlist_tracks);
            holder.p_tracks.setText(mPlaylistModels.get(position).getNumberOfTracks());
            //if a playlist is selected, open the list of songs up
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GenericSongList.class);
                    intent.putExtra("com.jmulla.musicplayer.SONGS", mPlaylistModels.get(position).getSongs());
                    intent.putExtra("NAME", mPlaylistModels.get(position).getName());
                    startActivity(intent);
                }
            });

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mActionMode != null) {
                        return false;
                    }
                    // Start the CAB using the ActionMode.Callback defined above
                    mActionMode = ((AppCompatActivity) getContext()).startSupportActionMode(mActionModeCallback);
                    v.setSelected(true);
                    return true;
                }
            });
            return rowView;
        }

        //class that holds the different views that are filled in
        private class Holder {
            TextView p_name;
            TextView p_tracks;
        }

    }

    //Class that loads up the playlists in the background. This is so the UI is not blocked and there is no visible lag
    public class ReloadPlaylists extends AsyncTask<Void, Integer, ArrayList<PlaylistModel>> {
        ProgressDialog pd_ring;
        View mView;
        ArrayList<PlaylistModel> playlistModels;
        Context mContext;
        ReloadPlaylists(View view) {
            this.mView = view;
        }

        ReloadPlaylists(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            pd_ring = new ProgressDialog(mContext);
            pd_ring.setMessage("Loading...");
            pd_ring.show();
        }

        @Override
        protected ArrayList<PlaylistModel> doInBackground(Void... params) {
            playlistModels = manager.getPlaylists(mContext);
            return playlistModels;
        }

        protected void onPostExecute(ArrayList<PlaylistModel> result) {
            //set the list of items to what we just got
            adapter.swapItems(result);
            if (pd_ring != null) {
                pd_ring.dismiss();
            }

        }
    }
}