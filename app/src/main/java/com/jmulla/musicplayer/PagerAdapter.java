package com.jmulla.musicplayer;

/**
 * Created by Jamal on 13/07/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    PlaylistsTab playlistsTab;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return Listfragment.newInstance();
            case 1:
                return ArtistsTab.newInstance();
            case 2:
                return AlbumTab.newInstance();
            case 3:
                PlaylistsTab p = PlaylistsTab.newInstance();
                playlistsTab = p;
                return p;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
