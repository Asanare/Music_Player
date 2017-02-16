package com.jmulla.musicplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jamal on 13/07/2016.
 */
public class PlaylistsTab extends Fragment{
    PlaylistsTabAdapter adapter;
    ListView lv;
    Button btn_newPlaylist;
    Manager manager = new Manager();
    ActionMode mActionMode;
    ArrayList<PlaylistModel> playlistModels = new ArrayList<>();

    public static PlaylistsTab newInstance() {
        return new PlaylistsTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.playlists_tab, container, false);
        init(view);
        return view;
    }

    void createFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment = new PlaylistsTab();
        transaction.replace(R.id.playlists_container, newFragment);
        transaction.commit();
    }

    void reload() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(PlaylistsTab.this).attach(PlaylistsTab.this).commit();
    }

    void init(View view) {

        lv = (ListView) view.findViewById(R.id.lv_playlists);
        btn_newPlaylist = (Button) view.findViewById(R.id.btn_new_playlist);
        playlistModels = manager.getPlaylists(getContext());
        TextView tvPlaylists = (TextView) view.findViewById(R.id.tv_no_playlists);
        adapter = new PlaylistsTabAdapter(getContext(), playlistModels);
        CurrentSong.makeToast(getContext(), "LIKE NEW");

        if (playlistModels.size() == 0) {
            lv.setVisibility(View.INVISIBLE);
            tvPlaylists.setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.VISIBLE);
            tvPlaylists.setVisibility(View.INVISIBLE);
        }
        lv.setAdapter(adapter);

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

    class PlaylistsTabAdapter extends BaseAdapter {
        private ArrayList<PlaylistModel> mPlaylistModels;
        private Context mContext;
        private LayoutInflater inflater = null;

        PlaylistsTabAdapter(Context context, ArrayList<PlaylistModel> playlistModels) {
            // TODO Auto-generated constructor stub
            mPlaylistModels = playlistModels;
            mContext = context;
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder;
            View rowView = convertView;
            final ActionMode.Callback mActionModeCallback;
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
                            db.deletePlaylist(playlistModels.get(position));
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

            holder.p_name = (TextView) rowView.findViewById(R.id.playlist_name);
            holder.p_name.setText(mPlaylistModels.get(position).getName());
            holder.p_tracks = (TextView) rowView.findViewById(R.id.playlist_tracks);
            holder.p_tracks.setText(mPlaylistModels.get(position).getNumberOfTracks());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GenericSongList.class);
                    intent.putExtra("com.jmulla.musicplayer.SONGS", playlistModels.get(position).getSongs());
                    intent.putExtra("NAME", playlistModels.get(position).getName());
                    startActivity(intent);
                    //Toast.makeText(mContext, "You Clicked "+mPlaylistModels.get(position).getName(), Toast.LENGTH_LONG).show();
                }
            });

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mActionMode != null) {
                        return false;
                    }

                    // Start the CAB using the ActionMode.Callback defined above
                    //mActionMode = getActivity().startActionMode(mActionModeCallback);
                    mActionMode = ((AppCompatActivity) getContext()).startSupportActionMode(mActionModeCallback);
                    v.setSelected(true);
                    return true;
                }
            });

            return rowView;
        }

        private class Holder {
            TextView p_name;
            TextView p_tracks;
        }

    }
}