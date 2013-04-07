package com.udav.extras.liveview.plugins;

import com.udav.extras.liveview.plugins.myweather.Parser;
import com.udav.mymeatherplugin.R;

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
        
        String city[] = new String[Parser.resultCity.size()], 
        		cityID[] = new String[Parser.resultCityID.size()];
        for (int i=0; i<Parser.resultCity.size(); i++) {
        	city[i] = Parser.resultCity.get(i);
        	cityID[i] = Parser.resultCityID.get(i);
        }
        selectCity.setEntries(city);
        selectCity.setEntryValues(cityID);
        rootScreen.addPreference(selectCity);
        
        addPreferencesFromResource(getResources().getIdentifier("preferences", "xml", getPackageName()));
    }
	
}