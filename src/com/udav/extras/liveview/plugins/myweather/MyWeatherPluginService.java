package com.udav.extras.liveview.plugins.myweather;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.udav.extras.liveview.plugins.AbstractPluginService;
import com.udav.extras.liveview.plugins.PluginConstants;
import com.udav.extras.liveview.plugins.PluginUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyWeatherPluginService extends AbstractPluginService {
	private Weather w;
	private Timer timer;
	private int updateInterval;
	private String cityID;
	private int index = -1;
	private ArrayList<ForecastWeather> forecast;
    
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		System.out.println("I'm Started!");		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		android.os.Debug.waitForDebugger();
		
		if (DBHelper.getDataFromDB(getBaseContext()).getCount() == 0)
		new Thread() {
			@Override
			public void run(){
				while (!isNetworkAvailable()){
					try {
						Thread.sleep(1000*60*15);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Parser.parseCity(getBaseContext());
			}
		}.start();
		w = new Weather();
		timer = new Timer();
		this.setPreferences();
		updateInterval = Integer.parseInt(mSharedPreferences.getString("updateInt", "15"));
		cityID = mSharedPreferences.getString("cityPref", "28698");
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				if (isNetworkAvailable()) {
				//set city id // get it this http://weather.yandex.ru/static/cities.xml
					w = Parser.weatherParse(cityID);
					forecast = Parser.parseForecast(cityID);
					System.out.println("update!!!");
					//for (int i=0; i<Parser.forecast.size(); i++)
					//	System.out.println(Parser.forecast.get(i).toString());
				}
			}
		}, 0, updateInterval*60*1000); 
		//run thread where update weather data
		System.out.println("I'm created!"); 
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();	
		stopWork();
	}
	
    /** 
     * Plugin is sandbox.
     */
    protected boolean isSandboxPlugin() {
        return true;
    }
	
	/**
	 * Must be implemented. Starts plugin work, if any.
	 */
	protected void startWork() {
		//show data
		System.out.println(w.toString());
		PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
		System.out.println("I'm start work!");
	}
	
	/**
	 * Must be implemented. Stops plugin work, if any.
	 */
	protected void stopWork() {
		System.out.println("I'm stop work!"); 
	}
	
	/**
	 * Must be implemented.
	 * 
	 * PluginService has done connection and registering to the LiveView Service. 
	 * 
	 * If needed, do additional actions here, e.g. 
	 * starting any worker that is needed.
	 */
	protected void onServiceConnectedExtended(ComponentName className, IBinder service) {
		
	}
	
	/**
	 * Must be implemented.
	 * 
	 * PluginService has done disconnection from LiveView and service has been stopped. 
	 * 
	 * Do any additional actions here.
	 */
	protected void onServiceDisconnectedExtended(ComponentName className) {
		
	}

	/**
	 * Must be implemented.
	 * 
	 * PluginService has checked if plugin has been enabled/disabled.
	 * 
	 * The shared preferences has been changed. Take actions needed. 
	 */	
	protected void onSharedPreferenceChangedExtended(SharedPreferences prefs, String key) {
		updateInterval = Integer.parseInt(prefs.getString("updateInt", "15"));
		cityID = prefs.getString("cityPref", "28698");
		
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				if (isNetworkAvailable()) {
					w = Parser.weatherParse(cityID);
					forecast = Parser.parseForecast(cityID);
				}
			}
		}, 0, updateInterval*60*1000); 
	}

	protected void startPlugin() {
		Log.d(PluginConstants.LOG_TAG, "startPlugin");
		startWork();
	}
			
	protected void stopPlugin() {
		Log.d(PluginConstants.LOG_TAG, "stopPlugin");
		stopWork();
	}
	
	protected void button(String buttonType, boolean doublepress, boolean longpress) {
	    Log.d(PluginConstants.LOG_TAG, "button - type " + buttonType + ", doublepress " + doublepress + ", longpress " + longpress);
		
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_UP)) {
		    if(longpress) {
		        //mLiveViewAdapter.ledControl(mPluginId, 50, 50, 50);
		    } else {
		    }
		} else if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_DOWN)) {
            if(longpress) {
                //mLiveViewAdapter.vibrateControl(mPluginId, 50, 50);
            } else {
            	
            }
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_RIGHT)) {
			index++;
			if (index >= forecast.size()) {
				index = -1;
				PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
			} else
			PluginUtils.displayForecastWeather(getBaseContext(), mLiveViewAdapter, mPluginId, forecast.get(index), 14);
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_LEFT)) {
			index--;
			if (index == -1) {
				PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
			} else {
				if (index < -1) {
					index = forecast.size()-1;
				}
				PluginUtils.displayForecastWeather(getBaseContext(), mLiveViewAdapter, mPluginId, forecast.get(index), 14);
			}
			
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_SELECT)) {
			if (isNetworkAvailable()){
				w = Parser.weatherParse(cityID);
				Parser.parseForecast(cityID);
			}
			PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
		}
	}

	protected void displayCaps(int displayWidthPx, int displayHeigthPx) {
        Log.d(PluginConstants.LOG_TAG, "displayCaps - width " + displayWidthPx + ", height " + displayHeigthPx);
    }

	protected void onUnregistered() throws RemoteException {
		Log.d(PluginConstants.LOG_TAG, "onUnregistered");
		stopWork();
	}

	protected void openInPhone(String openInPhoneAction) {
		Log.d(PluginConstants.LOG_TAG, "openInPhone: " + openInPhoneAction);
	}
	/**
	 * Called when screen change status
	 */
    protected void screenMode(int mode) {
        Log.d(PluginConstants.LOG_TAG, "screenMode: screen is now " + ((mode == 0) ? "OFF" : "ON"));
    }
    
    /**
     * check network connection
     * @return
     */
    public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}

    
    
}