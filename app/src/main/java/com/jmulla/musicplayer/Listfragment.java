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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Listfragment extends Fragment {
    static ListView lv_search;
    static SongsListAdapter listAdapter;

    private static ArrayList<Pair<Long, Song>> mItemArray = new ArrayList<>();
    private static MySwipeRefreshLayout mRefreshLayout;
    Manager manager = new Manager();
    SongsListAdapter array_adapter;
    ArrayList<Song> songData;
    TextView tv_count;
    ArrayList<Song> songInfo;
    ActionMode.Callback mActionModeCallback;
    View selectedItem;
    boolean hasLoaded = false;
    Spinner spinner;
    private DragListView mDragListView;

    public static Listfragment newInstance() {
        return new Listfragment();
    }

    public static void initSearchResults(ArrayList<Pair<String, ArrayList<Object>>> results, View view, Context context) {
        ArrayList<Object> result = new ArrayList<>();
        for (Pair<String, ArrayList<Object>> c : results) {
            result.add(c.first);
            for (Object s : c.second) {
                result.add(s);
            }
        }
        lv_search.setAdapter(new SearchAdapter(context, result));
    }

    public static void makeSearchVisible() {
        lv_search.setVisibility(View.VISIBLE);
    }

    public static void makeSearchInvisible() {
        lv_search.setVisibility(View.INVISIBLE);
    }

    public static void makeQueueVisible() {
        mRefreshLayout.setVisibility(View.VISIBLE);
    }

    public static void makeQueueInvisible() {
        mRefreshLayout.setVisibility(View.INVISIBLE);
    }

    public static void sortAlphabetical() {
        ArrayList<Pair<Long, Song>> temp = new ArrayList<>(mItemArray);
        Collections.sort(temp, new Comparator<Pair<Long, Song>>() {
            @Override
            public int compare(final Pair<Long, Song> o1, final Pair<Long, Song> o2) {
                String songName1 = o1.second.title.toUpperCase();
                String songName2 = o2.second.title.toUpperCase();
                return songName1.compareTo(songName2);
            }
        });
        //listAdapter.removeList();
        listAdapter.setItemList(temp);
/*        for (int i = 0; i < mItemArray.size(); i++){
            listAdapter.notifyItemChanged(i);
        }*/
    }

    public static void sortReverseAlphabetical() {
        ArrayList<Pair<Long, Song>> temp = new ArrayList<>(mItemArray);
        Collections.sort(temp, new Comparator<Pair<Long, Song>>() {
            @Override
            public int compare(final Pair<Long, Song> o1, final Pair<Long, Song> o2) {
                String songName1 = o1.second.title.toUpperCase();
                String songName2 = o2.second.title.toUpperCase();
                return songName2.compareTo(songName1);
            }
        });
        //listAdapter.removeList();
        listAdapter.setItemList(temp);

 /*       for (int i = 0; i < mItemArray.size(); i++){
            listAdapter.notifyItemChanged(i);
        }*/
        //listAdapter.notifyDataSetChanged();
        //TODO fix the "ghost" issue.
    }

    public static void sortDurationH2L() {
        ArrayList<Pair<Long, Song>> temp = new ArrayList<>(mItemArray);
        Collections.sort(temp, new Comparator<Pair<Long, Song>>() {
            @Override
            public int compare(final Pair<Long, Song> o1, final Pair<Long, Song> o2) {
                int songDur1 = Integer.parseInt(o1.second.duration);
                int songDur2 = Integer.parseInt(o2.second.duration);
                return Integer.compare(songDur2, songDur1);
            }
        });
        //listAdapter.removeList();
        listAdapter.setItemList(temp);
/*        for (int i = 0; i < mItemArray.size(); i++){
            listAdapter.notifyItemChanged(i);
        }*/
    }

    public static void sortDurationL2H() {
        ArrayList<Pair<Long, Song>> temp = new ArrayList<>(mItemArray);
        Collections.sort(temp, new Comparator<Pair<Long, Song>>() {
            @Override
            public int compare(final Pair<Long, Song> o1, final Pair<Long, Song> o2) {
                int songDur1 = Integer.parseInt(o1.second.duration);
                int songDur2 = Integer.parseInt(o2.second.duration);
                return Integer.compare(songDur1, songDur2);
            }
        });
        //listAdapter.removeList();
        listAdapter.setItemList(temp);
/*        for (int i = 0; i < mItemArray.size(); i++){
            listAdapter.notifyItemChanged(i);
        }*/
    }

    public static void sortArtistA2Z() {
        ArrayList<Pair<Long, Song>> temp = new ArrayList<>(mItemArray);
        Collections.sort(temp, new Comparator<Pair<Long, Song>>() {
            @Override
            public int compare(final Pair<Long, Song> o1, final Pair<Long, Song> o2) {
                String songArtist1 = o1.second.artist.toUpperCase();
                String songArtist2 = o2.second.artist.toUpperCase();
                return songArtist1.compareTo(songArtist2);
            }
        });

        listAdapter.setItemList(temp);
        //listAdapter.removeList();
        //listAdapter.setItemList(mItemArray);
/*        for (int i = 0; i < mItemArray.size(); i++){
            listAdapter.notifyItemChanged(i);
        }*/
    }

    public static void sortArtistZ2A() {
        ArrayList<Pair<Long, Song>> temp = new ArrayList<>(mItemArray);
        Collections.sort(temp, new Comparator<Pair<Long, Song>>() {
            @Override
            public int compare(final Pair<Long, Song> o1, final Pair<Long, Song> o2) {
                String songArtist1 = o1.second.artist.toUpperCase();
                String songArtist2 = o2.second.artist.toUpperCase();
                return songArtist2.compareTo(songArtist1);
            }
        });
        listAdapter.setItemList(temp);
        //listAdapter.removeList();
        //listAdapter.setItemList(mItemArray);
/*        for (int i = 0; i < mItemArray.size(); i++){
            listAdapter.notifyItemChanged(i);
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        CurrentSong.makeToast(getContext(), "Resumed list fragment");
        new ScanForAudio().execute();
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
        CurrentSong.makeToast(getContext(), "RELOADED MATRIX");
        //tv_count = (TextView) view.findViewById(R.id.tv_count);
        new ScanForAudio(view).execute();
        //getSongInfo();
        //Log.d("SIZE", String.valueOf(songInfo.size()));
/*        for (int i = 0; i< songInfo.size(); i++){
            mItemArray.add(new Pair<>((long) i, songInfo.get(i)));
        }*/


        // put the ListView in the pop up
        initialiseLayouts(view);
        setupListRecyclerView();

        return view;
    }

    void initialiseLayouts(View view) {
        mRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mDragListView = (DragListView) view.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        lv_search = (ListView) view.findViewById(R.id.lv_search);
        mDragListView.getRecyclerView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentSong.makeToast(v.getContext(), "LONOOOOOOOOOOOOOOOOOOOOOOO");
                v.setSelected(true);
            }
        });

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
/*                if (CurrentSong.audioService != null) {
                    Manager.currentSongList = listAdapter.allSongs;
                }*/
            }

        });


        mRefreshLayout.setScrollingView(mDragListView.getRecyclerView());
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
/*                        ArrayList<Song> songs = manager.getAllMusicData(getContext());
                        mItemArray.clear();
                        for (int i = 0; i < songs.size(); i++) {
                            mItemArray.add(new Pair<>((long) i, songs.get(i)));
                        }
                        reload();*/
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

    }

    private void setupListRecyclerView() {
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new SongsListAdapter(mItemArray, R.layout.songs_list_item, R.id.image, false, getContext());
        mDragListView.setAdapter(listAdapter, false);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.songs_list_item));
    }

    public void reload() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(Listfragment.this).attach(Listfragment.this).commit();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.cancel(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Manager.audioService != null)
            try {
                Manager.audioService.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        }
    }

    public class ScanForAudio extends AsyncTask<Void, Integer, ArrayList<Song>> {
        ProgressDialog pd_ring;
        View mView;

        ScanForAudio(View view) {
            this.mView = view;
        }

        ScanForAudio() {

        }
        @Override
        protected void onPreExecute() {
            pd_ring = new ProgressDialog(getContext());
            pd_ring.setMessage("Loading...");
            pd_ring.show();
        }

        @Override
        protected ArrayList<Song> doInBackground(Void... params) {
            songData = manager.getAllMusicData(getContext());
            return songData;
        }

        protected void onPostExecute(ArrayList<Song> result) {

            mItemArray.clear();
            for (int i = 0; i < result.size(); i++) {
                mItemArray.add(new Pair<>((long) i, result.get(i)));
            }
            Manager.fullSongList = result;
            //listAdapter.setItemList(mItemArray);
            sortAlphabetical();
            if (pd_ring != null) {
                pd_ring.dismiss();
            }
            //initialiseLayouts(mView);
            //setupListRecyclerView();
            //tv_count.setText(String.format("%s songs", Integer.toString(result.size())));

        }
    }

}

