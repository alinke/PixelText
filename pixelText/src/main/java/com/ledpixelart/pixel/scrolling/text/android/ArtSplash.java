package com.ledpixelart.pixel.scrolling.text.android;

//import com.ledpixelart.pixel.scrolling.text.android.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;


/**
 * This is the Splash activity which is loaded when the application is invoked, it just displays the PIXEL logo
 */
public class ArtSplash extends Activity
{
	// Set the display time, in milliseconds (or extract it out as a configurable parameter)
	private final int SPLASH_DISPLAY_LENGTH = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		// Obtain the sharedPreference, default to true if not available
		//boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);
		//boolean isSplashEnabled = true;

		
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					//Finish the splash activity so it can't be returned to.
					ArtSplash.this.finish();
					// Create an Intent that will start the main activity.
					Intent mainIntent = new Intent(ArtSplash.this, ScrollingTextActivity.class);
					ArtSplash.this.startActivity(mainIntent);
				}
			}, SPLASH_DISPLAY_LENGTH);
		
	}
}