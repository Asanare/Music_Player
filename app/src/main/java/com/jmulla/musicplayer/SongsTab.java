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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SongsTab extends ListFragment {
    Manager manager = new Manager();
    SongsListAdapter array_adapter;
    ArrayList<ArrayList<String>> songData;
    TextView tv_count;
    ArrayList<Song> songInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songs_tab, container, false);
        tv_count = (TextView) view.findViewById(R.id.tv_count);
        ArrayList<Song> empty = new ArrayList<>();
        array_adapter = new SongsListAdapter(getContext(), empty);
        setListAdapter(array_adapter);

        new ScanForAudio().execute();
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
            songData = manager.getAllMusicData(getActivity(), 0);
            return songData;
        }

        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            pd_ring.dismiss();
            songInfo = Song.getSong(result);
            tv_count.setText(String.format("%s songs", Integer.toString(result.size())));
            array_adapter.clear();
            array_adapter.addAll(songInfo);
            array_adapter.notifyDataSetChanged();
        }
    }
}

