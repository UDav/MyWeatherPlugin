package com.udav.extras.liveview.plugins;

import com.udav.extras.liveview.plugins.myweather.DBHelper;
import com.udav.extras.liveview.plugins.myweather.Parser;
import com.udav.mymeatherplugin.R;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * Implements PreferenceActivity and sets the project preferences to the 
 * shared preferences of the current user session.
 */
public class PluginPreferences extends PreferenceActivity {
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(rootScreen);
        
        ListPreference selectCity = new ListPreference(this);
        selectCity.setKey("cityPref");
        selectCity.setTitle(getString(R.string.city));
        selectCity.setSummary(getString(R.string.city_sum));
        android.os.Debug.waitForDebugger();
        
        
        Cursor mCursor = DBHelper.getDataFromDB(getBaseContext());
        int count = mCursor.getCount();
        
        String city[] = new String[count], 
        		cityID[] = new String[count];
        mCursor.moveToFirst();
        for (int i=0; i<count; i++) {
        	city[i] = mCursor.getString(2);
        	cityID[i] = mCursor.getString(1);
        	mCursor.moveToNext();
        }
        selectCity.setEntries(city);
        selectCity.setEntryValues(cityID);
        rootScreen.addPreference(selectCity);
        
        addPreferencesFromResource(getResources().getIdentifier("preferences", "xml", getPackageName()));
    }
	
}