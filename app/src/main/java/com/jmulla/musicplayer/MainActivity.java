package com.jmulla.musicplayer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ListView lv_songs = (ListView) findViewById(R.id.lv_songs);
        //manager = new Manager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ActionBar actionBar = getSupportActionBar();

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            View view1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv1 = (TextView) view1.findViewById(R.id.tv_tab_title);
            tv1.setText("Artists");
            tabLayout.addTab(tabLayout.newTab().setText("Artists").setCustomView(view1));
            View view2 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv2 = (TextView) view2.findViewById(R.id.tv_tab_title);
            tv2.setText("Songs");
            tabLayout.addTab(tabLayout.newTab().setText("Songs").setCustomView(view2));
            View view3 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv3 = (TextView) view3.findViewById(R.id.tv_tab_title);
            tv3.setText("Playlists");
            tabLayout.addTab(tabLayout.newTab().setText("Playlists").setCustomView(view3));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        assert tabLayout != null;
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
        }
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }

        assert viewPager != null;
        viewPager.setCurrentItem(1);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
