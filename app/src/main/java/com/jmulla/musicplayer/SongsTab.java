package com.jmulla.musicplayer;

/**
 * Created by Jamal on 13/07/2016.
 */

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SongsTab extends ListFragment {
    Manager manager = new Manager();
    SongsListAdapter array_adapter;
    ArrayList<ArrayList<String>> songData;
    TextView tv_count;
    ArrayList<Song> songInfo;
    ListView listView;
    ActionMode.Callback mActionModeCallback;
    View selectedItem;
    @Override
    public void onStart() {
        super.onStart();

        mActionModeCallback = new ActionMode.Callback() {

            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
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
                return false;
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                unselect(selectedItem);
            }
        };
        listView = getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().startActionMode(mActionModeCallback);
                ((SongsListAdapter) parent.getAdapter()).selectedIds.clear();
                ArrayList<Integer> selectedIds = ((SongsListAdapter) parent.getAdapter()).selectedIds;
                if(selectedIds.contains(position)){
                    //view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    selectedIds.remove(position);
                }
                else {
                    selectedIds.add(position);
                    //view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }
                ((SongsListAdapter) parent.getAdapter()).notifyDataSetChanged();
                selectedItem = view;
                return true;
            }

        });
        new ScanForAudio().execute();
    }
    void unselect(View view){
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songs_tab, container, false);
        tv_count = (TextView) view.findViewById(R.id.tv_count);
        ArrayList<Song> empty = new ArrayList<>();
        array_adapter = new SongsListAdapter(getContext(), empty);
        setListAdapter(array_adapter);
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getContext(), CurrentSong.class);
        intent.putExtra("SONG_LOCATION", songInfo.get(position).location);
        intent.putExtra("SONG_ARTIST", songInfo.get(position).artist);
        intent.putExtra("SONG_DURATION", songInfo.get(position).duration);
        intent.putExtra("SONG_TITLE", songInfo.get(position).title);
        startActivity(intent);
    }
    private class ScanForAudio extends AsyncTask<Void, Integer, ArrayList<ArrayList<String>>> {
        ProgressDialog pd_ring;

        @Override
        protected void onPreExecute() {
            pd_ring = new ProgressDialog(getContext());
            pd_ring.setMessage("Loading...");
            pd_ring.show();

        }

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... params) {
            songData = manager.getAllMusicData(getContext(), 0);
            return songData;
        }

        protected void onPostExecute(ArrayList<ArrayList<String>> result) {

            songInfo = Song.getSong(result);
            tv_count.setText(String.format("%s songs", Integer.toString(result.size())));
            array_adapter.clear();
            array_adapter.addAll(songInfo);
            array_adapter.notifyDataSetChanged();
            pd_ring.dismiss();
        }
    }
}

