package com.udav.plugins.myweather;

import java.util.ArrayList;

import com.udav.plugins.AbstractPluginService;
import com.udav.plugins.PluginConstants;
import com.udav.plugins.PluginUtils;
import com.udav.plugins.containers.DBHelper;
import com.udav.plugins.containers.ForecastWeather;
import com.udav.plugins.containers.Weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyWeatherPluginService extends AbstractPluginService {
	private Weather w;
	private int updateInterval;
	private String cityID;
	private int index = -1;
	private int vIndex = 0;
	private ArrayList<ForecastWeather> forecast;
	private boolean update = true;
	private boolean changeData = false;
	private int numberOfDays;
    
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		
		
		Log.d(PluginConstants.LOG_TAG, "I'm Start!");
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.onStart(intent, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		w = DBHelper.getCurrentWeatherFromDB(getBaseContext());
		forecast = DBHelper.getForecastWeatherFromDB(getBaseContext());
		Parser.arrWeatherNextHours = DBHelper.getWeatherNextHoursFromDB(getBaseContext());
		
		/*//The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent("com.udav.extras.liveview.plugins.myweather.PREFERENCES");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);
		//This constructor is deprecated. Use Notification.Builder instead
		Notification notice = new Notification(R.drawable.wiz1, "WeatherPlugin", System.currentTimeMillis());
		//This method is deprecated. Use Notification.Builder instead.
		notice.setLatestEventInfo(this, "WeatherPlugin", "Settings plugin", pendIntent);
		notice.flags = Notification.FLAG_NO_CLEAR;
		this.startForeground(1337, notice);*/
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		if (!isAlreadyRunning()) {
			//w = new Weather();

			this.setPreferences();
			updateInterval = Integer.parseInt(mSharedPreferences.getString("updateIntPref", "15"));
			cityID = mSharedPreferences.getString("cityPref", "28698");
			update = mSharedPreferences.getBoolean("weatherUpdate", true);
			numberOfDays = Integer.parseInt(mSharedPreferences.getString("numberOfDays", "7"));
			
			BroadcastReceiver receiver = new BroadcastReceiver() {
	            @Override 
	            public void onReceive(Context context, Intent intent){
	            	if (isNetworkAvailable()) {
	            		Thread parseThread = new Thread(){
	            			@Override
	            			public void run(){
	            				Log.d(PluginConstants.LOG_TAG, "Start timer!");
	            				//set city id // get it this http://weather.yandex.ru/static/cities.xml
	            				long time = System.currentTimeMillis();
	            				w = Parser.weatherParse(getBaseContext(), cityID);
	            				forecast = Parser.parseForecast(getBaseContext(), cityID);
	            				changeData = true;
	            				Log.d(PluginConstants.LOG_TAG, "update! "+(System.currentTimeMillis()-time));
	            			}
	            		};
	            		parseThread.setPriority(Thread.MIN_PRIORITY);
	            		parseThread.start();
	            	} else {
	            		Log.d(PluginConstants.LOG_TAG, "not internet connection!");
	            	}
	            }
	        };

	        this.registerReceiver(receiver, new IntentFilter("com.udav.plugins.myweather.update") );
	        if (update) {
				setAlarm();
			} else {
				unsetAlarm();
				update();
			}
			
			BroadcastReceiver internetBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    ConnectivityManager connectivity = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);


                        NetworkInfo[] info = connectivity.getAllNetworkInfo();
                        
                        //check wifi and gsm internet connection 
                        if ((info[0].getState()==NetworkInfo.State.CONNECTED)||
                        		info[1].getState()==NetworkInfo.State.CONNECTED){
                        	if (updateState == false) setAlarm();
                        } else
                        	unsetAlarm();
                    }

                };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(internetBroadcastReceiver, intentFilter);

		}
		
		Thread cityThread = null;
		if (DBHelper.getDataFromDB(getBaseContext()).getCount() == 0){
			cityThread  = new Thread() {
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
			};
			cityThread.setPriority(Thread.MIN_PRIORITY);
			cityThread.start();
		}
		
		Log.d(PluginConstants.LOG_TAG, "I'm Created!");
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();	
		Log.d(PluginConstants.LOG_TAG, "I'm die! They destroy me!");
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
		//System.out.println(w.toString());
		if (w != null)
			PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
		else
			PluginUtils.displayNotConnection(mLiveViewAdapter, mPluginId);
		index = -1;
		vIndex = 0;
		changeData = false;
		Log.d(PluginConstants.LOG_TAG, "I'm start work!");
	}
	
	/**
	 * Must be implemented. Stops plugin work, if any.
	 */
	protected void stopWork() {
		Log.d(PluginConstants.LOG_TAG, "I,m stop work!");
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
		updateInterval = Integer.parseInt(prefs.getString("updateIntPref", "15"));
		cityID = prefs.getString("cityPref", "28698");
		update = prefs.getBoolean("weatherUpdate", true);
		numberOfDays = Integer.parseInt(mSharedPreferences.getString("numberOfDays", "7"));
		
		if (update) {
			setAlarm();
		} else {
			unsetAlarm();
			update();
		}
	}

	@Override
	public boolean stopService(Intent name) {
		Log.d(PluginConstants.LOG_TAG, "I'm die! They stoped me!");
		return super.stopService(name);
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
		    	if ((vIndex == 1) && (index == -1) && (w != null)) {
		    		PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
		    		vIndex = 0;
		    	}
		    }
		} else if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_DOWN)) {
            if(longpress) {
                //mLiveViewAdapter.vibrateControl(mPluginId, 50, 50);
            } else {
            	if ((vIndex == 0) && (Parser.arrWeatherNextHours != null) && (index == -1)) {
            		PluginUtils.displayWeatherNextHours(getBaseContext(), mLiveViewAdapter, mPluginId);
            		vIndex = 1;
            	}
            }
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_RIGHT)) {
			if (forecast.size() < numberOfDays) numberOfDays = forecast.size();
			
			if ((forecast != null) && (vIndex == 0)) {
				index++;
				if (index >= numberOfDays) {
					index = -1;
					PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
				} else {
					PluginUtils.displayForecastWeather(getBaseContext(), mLiveViewAdapter, mPluginId, forecast.get(index), 14);
				}
			}
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_LEFT)) {
			if (forecast.size() < numberOfDays) numberOfDays = forecast.size();
			
			if ((forecast != null) && (vIndex == 0)) {
				index--;
				if (index == -1) {
					PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
				} else {
					if (index < -1) {
						index = numberOfDays-1;
					}
					PluginUtils.displayForecastWeather(getBaseContext(), mLiveViewAdapter, mPluginId, forecast.get(index), 14);
				}
			}
			
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_SELECT)) {
			if (update) {
				setAlarm();
			} else {
				unsetAlarm();
				update();
			}
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
        if ((mode == 1) && (changeData)) {
        	if (w != null) {
				PluginUtils.displayWeather(getBaseContext(), mLiveViewAdapter, mPluginId, w, 14);
				index = -1; vIndex = 0;
				changeData = false;
			}
        }
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
    
    private boolean updateState = false;
    
    private void setAlarm(){
    	updateState = true;
        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.udav.plugins.myweather.update"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        manager.cancel(pintent);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), updateInterval*60*1000, pintent);
    }
    private void unsetAlarm(){
    	updateState = false;
    	PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.udav.plugins.myweather.update"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        manager.cancel(pintent);
    }
    private void update() {
    	sendBroadcast(new Intent("com.udav.plugins.myweather.update"));
    }

    
    
}