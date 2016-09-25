package com.jmulla.musicplayer;

/**
 * Created by Jamal on 13/07/2016.
 */


import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class Listfragment extends Fragment {
    Manager manager = new Manager();
    SongsListAdapter array_adapter;
    ArrayList<ArrayList<String>> songData;
    TextView tv_count;
    ArrayList<Song> songInfo;

    ActionMode.Callback mActionModeCallback;
    View selectedItem;
    boolean hasLoaded = false;
    private ArrayList<Pair<Long, Song>> mItemArray = new ArrayList<>();
    private DragListView mDragListView;
    private MySwipeRefreshLayout mRefreshLayout;
    SongsListAdapter listAdapter;
    public static Listfragment newInstance() {
        return new Listfragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    void unselect(View view) {
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);

        //tv_count = (TextView) view.findViewById(R.id.tv_count);
        new ScanForAudio(view).execute();
        //getSongInfo();
        //Log.d("SIZE", String.valueOf(songInfo.size()));
/*        for (int i = 0; i< songInfo.size(); i++){
            mItemArray.add(new Pair<>((long) i, songInfo.get(i)));
        }*/

        initialiseLayouts(view);
        setupListRecyclerView();

        return view;
    }

    void initialiseLayouts(View view) {
        mRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mDragListView = (DragListView) view.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                mRefreshLayout.setEnabled(false);
                //Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                mRefreshLayout.setEnabled(true);
                if (fromPosition != toPosition) {
                    //Toast.makeText(mDragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                }
                if (CurrentSong.audioService != null){
                    Manager.currentSongList = listAdapter.allSongs;
                }
            }

        });


        mRefreshLayout.setScrollingView(mDragListView.getRecyclerView());
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void setupListRecyclerView() {
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new SongsListAdapter(mItemArray, R.layout.songs_list_item, R.id.image, false);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.songs_list_item));
    }

    public void getSongInfo() {
        songData = manager.getAllMusicData(getContext(), 0);
        songInfo = Song.getSong(songData);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }
    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            ((TextView) (dragView.findViewById(R.id.tv_title))).setText(((TextView) clickedView.findViewById(R.id.tv_title)).getText());
            ((TextView) (dragView.findViewById(R.id.tv_artist))).setText(((TextView) clickedView.findViewById(R.id.tv_artist)).getText());
            ((TextView) (dragView.findViewById(R.id.tv_duration))).setText(((TextView) clickedView.findViewById(R.id.tv_duration)).getText());
            //CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            //((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.setBackgroundColor(ContextCompat.getColor(clickedView.getContext(),R.color.colorAccent));
        }
    }

    public class ScanForAudio extends AsyncTask<Void, Integer, ArrayList<ArrayList<String>>> {
        ProgressDialog pd_ring;
        View mView;
        ScanForAudio(View view){
            this.mView = view;
        }
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
            mItemArray = new ArrayList<>();
            for (int i = 0; i< songInfo.size(); i++){
                mItemArray.add(new Pair<>((long) i, songInfo.get(i)));
            }
            listAdapter.setItemList(mItemArray);
            listAdapter.notifyDataSetChanged();
            //initialiseLayouts(mView);
            //setupListRecyclerView();
            //tv_count.setText(String.format("%s songs", Integer.toString(result.size())));
            pd_ring.dismiss();
        }
    }
}

