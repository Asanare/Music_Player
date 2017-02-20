package com.jmulla.musicplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Manager manager;
    static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 0;
    MenuItem sortSpinner;
    Button search;
    Spinner spinner;
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ListView lv_songs = (ListView) findViewById(R.id.lv_songs);
        //manager = new Manager();
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
/*        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);*/
/*        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.blue_900));
        window.setNavigationBarColor(ContextCompat.getColor(getBaseContext(), R.color.blue_900));*/
        int hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                showMessageOKCancel("You need to allow access to the device storage",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        } else {
            initActivity();

        }
    }

    void initActivity() {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            View view1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv1 = (TextView) view1.findViewById(R.id.tv_tab_title);
            tv1.setText("Songs");
            tabLayout.addTab(tabLayout.newTab().setText("Songs").setCustomView(view1));

            View view2 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv2 = (TextView) view2.findViewById(R.id.tv_tab_title);
            tv2.setText("Artists");
            tabLayout.addTab(tabLayout.newTab().setText("Artists").setCustomView(view2));

            View view3 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv3 = (TextView) view3.findViewById(R.id.tv_tab_title);
            tv3.setText("Albums");
            tabLayout.addTab(tabLayout.newTab().setText("Albums").setCustomView(view3));

            View view4 = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv4 = (TextView) view4.findViewById(R.id.tv_tab_title);
            tv4.setText("Playlists");
            tabLayout.addTab(tabLayout.newTab().setText("Playlists").setCustomView(view4));

            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        assert tabLayout != null;
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        if (viewPager != null) {
            viewPager.setAdapter(adapter);
        }
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }
        assert viewPager != null;
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 3) {
                    try {
                        adapter.playlistsTab.onResumeFragment();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


/*                if(tab.getPosition() == 3){
                    adapter.notifyDataSetChanged();
                }*/

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initActivity();
                } else {
                    CurrentSong.makeToast(getApplicationContext(), "Permission was denied. Exiting");
                    this.finishAffinity();

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchButton);
        sortSpinner = menu.findItem(R.id.sort_spinner);
        SearchView searchView = (SearchView) searchItem.getActionView();
        spinner = (Spinner) sortSpinner.getActionView();


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        assert spinner != null;
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Alphabetical")) {
                    try {
                        Listfragment.sortAlphabetical();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (parent.getItemAtPosition(position).equals("Reverse Alphabetical")) {
                    Listfragment.sortReverseAlphabetical();
                } else if (parent.getItemAtPosition(position).equals("Duration - High to low")) {
                    Listfragment.sortDurationH2L();
                } else if (parent.getItemAtPosition(position).equals("Duration - Low to high")) {
                    Listfragment.sortDurationL2H();
                } else if (parent.getItemAtPosition(position).equals("Artist Name - A to Z")) {
                    Listfragment.sortArtistA2Z();
                } else if (parent.getItemAtPosition(position).equals("Artist Name - Z to A")) {
                    Listfragment.sortArtistZ2A();
                } else if (parent.getItemAtPosition(position).equals("Revert To Queue")) {

                } else {
                    Toast.makeText(getApplicationContext(), "Something has gone badly wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listfragment.changeLayout();
            }
        });*/

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Listfragment.makeQueueInvisible();
                Listfragment.makeSearchVisible();
                sortSpinner.setVisible(false);
                //Listfragment.changeLayout();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Write your code here
                Listfragment.makeSearchInvisible();
                Listfragment.makeQueueVisible();
                sortSpinner.setVisible(true);
                //Listfragment.changeLayout();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Listfragment.listAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_spinner:
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.blacklisted_option:
                Intent intent2 = new Intent(this, Blacklisted.class);
                startActivity(intent2);
                return true;
            case R.id.searchButton:

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
