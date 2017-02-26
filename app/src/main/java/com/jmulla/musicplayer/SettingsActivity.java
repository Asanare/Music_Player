package com.jmulla.musicplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/***
 * Created by Jamal on 14/01/2017.
 */
//Settings activity which is used to show all the options the user has
public class SettingsActivity extends PreferenceActivity {
    //creates the layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            Utilities.setThemeHere(getActivity());
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (!key.equals("theme")) {
                        return;
                    }
                    //if the theme changes, set the new theme
                    final Intent intent = getActivity().getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().finish();
                    Manager.needToRecreate = true;
                    getActivity().startActivity(intent);
                }
            };
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            prefs.registerOnSharedPreferenceChangeListener(mListener);
        }
    }
}
