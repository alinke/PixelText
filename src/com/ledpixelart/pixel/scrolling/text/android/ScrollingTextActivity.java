
package com.ledpixelart.pixel.scrolling.text.android;


import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import alt.android.os.CountDownTimer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.ledpixelart.pixel.hardware.Pixel;
/*
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
*/

@SuppressLint("ParserError")
public class ScrollingTextActivity extends IOIOActivity implements OnColorChangedListener 
{
	private TextView textView_;
	//private TextView scrollSpeedtextView_;	
	private SeekBar scrollSpeedSeekBar_;	
	private SeekBar fontSizeSeekBar_;	
	//private ToggleButton toggleButton_;	
	private static EditText textField;	
	private SharedPreferences prefs;
	private SharedPreferences savePrefs;
	private Editor mEditor;
	private Resources resources;
	private String app_ver;	
	private int matrix_model;
	private final String tag = "";	
	private final String LOG_TAG = "PixelText";
	private static int resizedFlag = 0;
	private ConnectTimer connectTimer; 	
	private static ScrollingTextTimer scrollingtextTimer_;
	private twitterTimer twitterTimer_;
  	private static int deviceFound = 0;
  	private boolean noSleep = false;	
	private int countdownCounter;
	private static final int countdownDuration = 30;
	private int scrollSpeed = 1;
	
	private static ioio.lib.api.RgbLedMatrix matrix_;
	private static ioio.lib.api.RgbLedMatrix.Matrix KIND;  //have to do it this way because there is a matrix library conflict
	private static android.graphics.Matrix matrix2;
	private static short[] frame_;
  	public static final Bitmap.Config FAST_BITMAP_CONFIG = Bitmap.Config.RGB_565;
  	private static byte[] BitmapBytes;
  	private static InputStream BitmapInputStream;
  	private static Bitmap canvasBitmap;
  	private static Bitmap IOIOBitmap;
  	private static Bitmap originalImage;
  	private static int width_original;
  	private static int height_original; 	  
  	private static float scaleWidth; 
  	private static float scaleHeight; 	  	
  	private static Bitmap resizedBitmap; 
	private Canvas canvas;
	private static Canvas canvasIOIO;
	
	private String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    private String basepath = extStorageDirectory;
    private static Context context;
    private Context frameContext;
	private boolean debug_;
	private static int appAlreadyStarted = 0;
	//private int scrollSpeedProgress = 1;
	private static int scrollSpeedValue = 100;
	private int fontSizeValue = 26;
	
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private Button writeButton_;
	private TextView text;
	private RadioButton speedRadio1;
	private RadioButton speedRadio2;
	private RadioButton speedRadio3;
	private RadioButton speedRadio4;
	private RadioGroup speedRadioGroup_;
	private SeekBar VerticalPositionSeekBar;
	private Spinner fontSpinner;
	
	private static int ColorWheel;
	private static Paint paint;
	private Typeface tf;
	private static String scrollingText; //used for scrolling text
	private static Rect bounds;
	private static int resetX;
	private static int messageWidth;
	private static int x  = 0;	
	private int stepSize = 6;
	private String prefFontSize;
	private int prefColor;
	private String prefScrollingText;
	
	private Typeface selectedFont;
	private String fontlist[];
	private int prefFontPosition;
	public long frame_length;
	private static int currentResolution;
	private static String pixelFirmware = "Not Connected";
	private static String pixelBootloader = "Not Connected";
	private static String pixelHardwareID = "Not Connected";
	private static String IOIOLibVersion = "Not Connected";
	private static VersionType v;
    private volatile static Timer timer;
    private static Pixel pixel;
    //private RgbLedMatrix ledMatrix;
    private static int scrollingKeyFrames_ = 1;
	private static final int REQUEST_PAIR_DEVICE = 10;
	private  ProgressDialog progress;
	private int yCenter;  //TO DO this center doesn't work all the time, add a way for the user to override up or down
	private static final int WENT_TO_PREFERENCES = 1;
	private int prefYoffset_;
	private int yOffset = 0;
	private String prefScrollSpeed_;
	private int fontSizeStepper = 8;
	
	private static Twitter twitter;
	    
	private static TwitterFactory twitterFactory;
	    
	private static Query query;
	    
	private static QueryResult result = null;
	    
    private Status status;
    
    private String lastTweet;
    
    private Integer tweetCount = 0;
    
    private static boolean filterTweets = true;
    
    private String twitterSearchString = "cats";
    
    private static boolean twitterMode = false;
	
	private static int twitterInterval = 60; //in seconds
	
	private static String twitterResult = null;
	
	private boolean AutoSelectPanel_ = true;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        
    	//TO DO this app should accept text sharing from other android apps
    	
    	//after ioio setup, we'll call the method with the timer to scroll the text, this timer will just keep running until we stop it
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       
        try
	        {
	            app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
	        }
        catch (NameNotFoundException e)
	        {
	            Log.v(tag, e.getMessage());
	        }
        
        //******** preferences code
        resources = this.getResources();
        setPreferences();
        //***************************
        
        textField = (EditText) findViewById(R.id.textField);  //the scrolling text
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
    	prefs = getSharedPreferences("appSave", MODE_PRIVATE);
        prefFontSize = prefs.getString("fontKey", "14");
       // prefYoffset_ = prefs.getInt("prefYoffset", KIND.height/2); //default to in the middle
        
        prefYoffset_ = prefs.getInt("prefYoffset", KIND.height/2); //default to in the middle
        yOffset = prefYoffset_ - KIND.height/2; //16 - 32/2 = 0 or 20 - 16 = 4
        
        prefScrollSpeed_ = prefs.getString("prefScrollSpeed", "1");
        prefScrollingText = prefs.getString("scrollingTextKey","TYPE TEXT HERE");
        prefColor = prefs.getInt("colorKey", 333333);
        prefFontPosition = prefs.getInt("fontPositionKey", 0);
        
        textField.setText(prefScrollingText);
       
        
        picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		button = (Button) findViewById(R.id.button1);
		writeButton_ = (Button) findViewById(R.id.writeButton);
		text = (TextView) findViewById(R.id.textView1);
		
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);
		
		VerticalPositionSeekBar = (SeekBar)findViewById(R.id.VerticalBar);
	    VerticalPositionSeekBar.setMax(KIND.height); //maximum for y offset is 32 for pixel and 64 for super pixel 
	    VerticalPositionSeekBar.setProgress(prefYoffset_); //start in the middle
	    
		if (isNetworkAvailable()) {
	    
		    ConfigurationBuilder cb = new ConfigurationBuilder();
		   	 cb.setDebugEnabled(true)
		   	   .setOAuthConsumerKey("Ax6lCfg9Yf2Niab22e9SsY75b")
		   	   .setOAuthConsumerSecret("3isp024VgehfZ60HwbEcBt1ZZzPyoXseaWYmO4NXxoxefKY65A")
		   	   .setOAuthAccessToken("") // we don't need these right now as we are just calling public twitter searches
		   	   .setOAuthAccessTokenSecret("");
		   	twitterFactory = new TwitterFactory(cb.build());
		   	twitter = twitterFactory.getInstance();
	   	
		}
	   	
	   
	    VerticalPositionSeekBar.setOnSeekBarChangeListener(OnSeekBarProgress);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				text.setTextColor(picker.getColor());
				picker.setOldCenterColor(picker.getColor());
			}
		});
		
		writeButton_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scrollTextButtonWrite(); //this method will first check if pixel was detected and if so, we'll write the text that is there
			}
		});
      
	    //   scrollSpeedtextView_ = (TextView)findViewById(R.id.scrollSpeedtextView);
        scrollSpeedSeekBar_ = (SeekBar)findViewById(R.id.SeekBar);
        scrollSpeedSeekBar_.setOnSeekBarChangeListener(OnSeekBarProgress);
        scrollSpeedSeekBar_.setMax(10);
        scrollSpeedSeekBar_.setProgress(Integer.parseInt(prefScrollSpeed_.toString()));  
		scrollingKeyFrames_ = Integer.parseInt(prefScrollSpeed_.toString()) + 1; //have to add the one because we can't have 0 which is the minimum scroll value
        
		System.out.println("scrolling frames: " + (scrollSpeedSeekBar_.getProgress() + 1) );
		
        fontSizeSeekBar_ = (SeekBar)findViewById(R.id.FontSeekBar);
        fontSizeSeekBar_.setOnSeekBarChangeListener(OnSeekBarProgress);
        fontSizeSeekBar_.setMax(96);
        fontSizeSeekBar_.setProgress(Integer.parseInt(prefFontSize));
        
        if (noSleep == true) {        	      	
        	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //disables sleep mode
        }	
        
        connectTimer = new ConnectTimer(30000,5000); //pop up a message if it's not connected by this timer
 		connectTimer.start(); //this timer will pop up a message box if the device is not found
 		
 		context = getApplicationContext();
        enableUi(true);
        
        bounds = new Rect();
        
        paint = new Paint();
        
    	if (prefColor != 333333) {   //let's set the last color from prefs
    		ColorWheel = prefColor;
    		paint.setColor(prefColor); 
    	}
    	else {
    		ColorWheel = Color.GREEN;
        	paint.setColor(ColorWheel);
    	}
        	//paint.setTypeface(tf);
    	
    	
    	 // Get intent, action and MIME type
	      Intent intent = getIntent();
	      String action = intent.getAction();
	      String type = intent.getType();

	      if (Intent.ACTION_SEND.equals(action) && type != null) {
	          if ("text/plain".equals(type)) {
	              handleSendText(intent); // Handle text being sent
	              //TO DO need to save this to preferences
	          } 
	      }    
    	
        //**** for the font drop down list
        
        fontSpinner = (Spinner) findViewById(R.id.fontSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        this, R.array.font_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);
        fontSpinner.setOnItemSelectedListener(new OnItemSelectedListener()

        {

        public void onItemSelected(AdapterView<?> arg0, View arg1,
        int arg2, long arg3)

	    {
	
        	if (deviceFound == 1 && pixelHardwareID.substring(0,4).equals("PIXL")) pixel.interactiveMode(); //need to add this as we could have been playing in local mode from writing to the local SD card
        	
        	int index = arg0.getSelectedItemPosition();
	
	        // storing string resources into Array
	        fontlist = getResources().getStringArray(R.array.font_options);
	        
	       //setFont is a function below in the code which sets the font based on the position passed
	        setFont(index);
	        
	        if(scrollingtextTimer_ != null) {
       		 	resetScrolling();
       	 	}
	        
	        //let's also save the font for the next time
	    	mEditor = prefs.edit();
            mEditor.putInt("fontPositionKey", index);
            mEditor.commit();
	      
	    }

        public void onNothingSelected(AdapterView<?> arg0) {
        	

        }

        });
    	
      //let's set the font here from prefs 
   	
        setFont(prefFontPosition);
        //we have the default font set so let's also set the spinner position so the user knows
        fontSpinner.setSelection(prefFontPosition);
    	int prefFontSizeNum = Integer.parseInt(prefFontSize.toString());
        prefFontSizeNum = ((int)Math.round(prefFontSizeNum/stepSize))*stepSize + fontSizeStepper;
		paint.setTextSize(prefFontSizeNum);
    	//paint.setFlags(Paint.ANTI_ALIAS_FLAG);  //important to remove this , we don't want anti-aliasing for pixel art
		
    	textField.addTextChangedListener(new TextWatcher(){  //had to add this , without it the text will disappear sometimes when charcters are removed, x becomes higher than the message length
            public void afterTextChanged(Editable s) {
            	
            	if (deviceFound == 1 && pixelHardwareID.substring(0,4).equals("PIXL")) pixel.interactiveMode(); //need to add this as we could have been playing in local mode from writing to the local SD card
            	//now let's save the user's entered scrolling text so the next time the app comes up, they don't have to re-type
            	
            	if(scrollingtextTimer_ != null) {
            		 resetScrolling();
            	 }
            	
            	mEditor = prefs.edit();
                mEditor.putString("scrollingTextKey", textField.getText().toString());
                mEditor.commit();
            	
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
    	
    	if (isNetworkAvailable()) {
    	
	    	if (twitterMode) {
		    	twitterTimer_ = new twitterTimer (100000,twitterInterval*1000);  //the prefs is in seconds so we'll need to convert to milliseconds
		    	twitterTimer_.start();
	    	}
    	}
    	else {
    		showToast("Twitter mode is on but no Internet");
    	}
    	
    }  //end oncreate

    OnSeekBarChangeListener OnSeekBarProgress =
        	new OnSeekBarChangeListener() {

        	public void onProgressChanged(SeekBar s, int progress, boolean touch){
        	
	            if(touch){
	        	
	            	if(s == scrollSpeedSeekBar_){
		            		
	            		if (deviceFound == 1 && pixelHardwareID.substring(0,4).equals("PIXL")) pixel.interactiveMode();
            			int progressPlus1 = progress + 1; //because we can't have 0
            			//scrollSpeedValue = (11 - progress) * 10;
		        		//scrollSpeedtextView_.setText(Integer.toString(progress));
	            		scrollingKeyFrames_ = progressPlus1;
		        		mEditor = prefs.edit();
		                mEditor.putString("prefScrollSpeed", String.valueOf(progress));
		                //mEditor.putInt("scrollSpeedKey", progres));
		                mEditor.commit();
	                
		                if(scrollingtextTimer_ != null) {
	               		 	resetScrolling();
	               	 	}
	            	}
	            	    
	            	if(s == fontSizeSeekBar_) {
	            		if (deviceFound == 1 && pixelHardwareID.substring(0,4).equals("PIXL")) pixel.interactiveMode();
	            		int rawProgress = progress;
	            		progress = ((int)Math.round(progress/stepSize))*stepSize + fontSizeStepper;
	            		fontSizeValue = progress;
	            		paint.setTextSize(fontSizeValue);
	            		
	            	    mEditor = prefs.edit();
	            		mEditor.putString("fontKey", String.valueOf(rawProgress));
	            		mEditor.commit();
	            	    //showToast("font size: " + String.valueOf(fontSizeValue));
	            		//x=64;
	            		//x=KIND.width *2 ;
	            		if(scrollingtextTimer_ != null) {
	               		 	resetScrolling();
	               	 	}
		            }
	            	
	            	if(s == VerticalPositionSeekBar) {
	            		if (deviceFound == 1 && pixelHardwareID.substring(0,4).equals("PIXL")) pixel.interactiveMode();
	            		//int rawProgress = progress;
	            		//progress = ((int)Math.round(progress/stepSize))*stepSize + 16;
	            		yOffset = progress - KIND.height/2; //16 - 32/2 = 0 or 20 - 16 = 4
	            	    mEditor = prefs.edit();
	            		mEditor.putInt("prefYoffset", progress); 
	            		mEditor.commit();
	            		if(scrollingtextTimer_ != null) {
	               		 	resetScrolling();
	               	 	}
		            }
	            }
      }
        	
           
	
    
        	public void onStartTrackingTouch(SeekBar s){

        	}

        	public void onStopTrackingTouch(SeekBar s){

        	}
    };
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private  void stopTimers() {
    	if (twitterTimer_ != null) twitterTimer_.cancel();
    }
    
    @SuppressLint("ParserError")
	private void handleSendText(Intent intent) {  //not used
	    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	    if (sharedText != null) {
	    	textField.setText(sharedText);
	    	mEditor = prefs.edit();
            mEditor.putString("scrollingTextKey", textField.getText().toString());
            mEditor.commit();
	    }
    }
    
    public void onColorChanged(int color) {
    	if (deviceFound == 1 && pixelHardwareID.substring(0,4).equals("PIXL")) pixel.interactiveMode();
    	ColorWheel = color;
    	//let's save the last color picked so the user doesn't have to re-enter next time they run the app
    	mEditor = prefs.edit();
 		mEditor.putInt("colorKey", ColorWheel);
 		mEditor.commit();
 		
 		
 		
 		if(scrollingtextTimer_ != null) {
   		 	resetScrolling();
   	 	}
    	
	}

	 
    private void resetScrolling() {
    	//x = 0; //having this works one scrolling sequence but then the next one doesn't come
    	x=KIND.width *2 ; //like this so the scrolling start at the edge
	 	scrollingtextTimer_.cancel();
		paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
        scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
    	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
    	yCenter = (KIND.height / 2) + ((bounds.height())/2 + yOffset);
		//scrollingtextTimer_ = new ScrollingTextTimer (100000,scrollSpeedValue);  //scrollingKeyFrames_
		scrollingtextTimer_ = new ScrollingTextTimer (100000,scrollSpeedValue);  //scrollSpeedValue was changed, we hard code at 100
 		scrollingtextTimer_.start();
    }
    
   
    
    private void setFont(int fontPosition) {
    	
    	  switch (fontPosition)  {
	        
	        case 0:
	        	selectedFont = Typeface.create("Arial",Typeface.NORMAL); 
	        	break;
	        	
	        case 1:
	        	selectedFont = Typeface.create("Helvetica",Typeface.NORMAL); 
	        	break;	
	        	
	        case 2:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/garmond.ttf");
	        	break;		
	        	
	        case 3:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/handwriting.ttf");
	        	break;
	        	
	        case 4:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/handwriting2.ttf");
	        	break;	
	        	
	        case 5:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/neon80s.ttf");
	        	break;	
	        	
	        case 6:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/pixel.ttf");
	        	break;
	        	
	        case 7:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/bigbold.ttf");
	        	break;	
	        	
	        case 8:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/bonzai.ttf");
	        	break;	
	        	
	        case 9:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/cartoon.ttf");
	        	break;
	        	
	        case 10:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/cursive.ttf");
	        	break;	
	        	
	        case 11:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/christmas.ttf");
	        	break;		
	        	
	        case 12:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/cocktails.ttf");
	        	break;
	        	
	        case 13:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/grinch.ttf");
	        	break;	
	        	
	        case 14:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/invaders.ttf");
	        	break;	
	        	
	        case 15:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/seagram.ttf");
	        	break;		
	        	
	        case 16:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/serif.ttf");
	        	break;
	        	
	        case 17:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/serif-large.ttf");
	        	break;	
	        	
	        case 18:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/simple.ttf");
	        	break;	
	        	
	        case 19:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/simpleprint.ttf");
	        	break;	
	        	
	        case 20:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/smalltype.ttf");
	        	break;	
	        	
	        case 21:
	        	selectedFont = Typeface.createFromAsset(getAssets(), "fonts/braille.ttf");
	        	break;	
	        	
	        default:
	        	selectedFont = Typeface.create("Arial",Typeface.NORMAL);
	        	break;
      }
    	  //now set the font
    	  paint.setTypeface(selectedFont);
    }
    
    private void showToast(final String msg) {
	 		runOnUiThread(new Runnable() {
	 			@Override
	 			public void run() {
	 				Toast toast = Toast.makeText(ScrollingTextActivity.this, msg, Toast.LENGTH_LONG);
	                 toast.show();
	 			}
	 		});
	 	}  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.mainmenu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
       
		
      if (item.getItemId() == R.id.menu_instructions) {
 	    	AlertDialog.Builder alert=new AlertDialog.Builder(this);
 	      	alert.setTitle(getResources().getString(R.string.setupInstructionsStringTitle)).setIcon(R.drawable.icon).setMessage(getResources().getString(R.string.setupInstructionsString)).setNeutralButton(getResources().getString(R.string.OKText), null).show();
 	   }
    	
	  
      if (item.getItemId() == R.id.menu_btPair)
      {
			
		if (pixelHardwareID.substring(0,4).equals("MINT")) { //then it's a PIXEL V1 unit
			showToast("Bluetooth Pair to PIXEL using code: 4545");
		}
		else { //we have a PIXEL V2 unit
			showToast("Bluetooth Pair to PIXEL using code: 0000");
		}
		
	  Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
      startActivityForResult(intent, REQUEST_PAIR_DEVICE);
       
      }
      
      if (item.getItemId() == R.id.menu_about) {
		  
		    AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      	alert.setTitle(getString(R.string.menu_about_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.menu_about_summary) + "\n\n" + getString(R.string.versionString) + " " + app_ver + "\n"
	      			+ getString(R.string.FirmwareVersionString) + " " + pixelFirmware + "\n"
	      			+ getString(R.string.HardwareVersionString) + " " + pixelHardwareID + "\n"
	      			+ getString(R.string.BootloaderVersionString) + " " + pixelBootloader + "\n"
	      			+ getString(R.string.LibraryVersionString) + " " + IOIOLibVersion).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
	   }
    	
    	if (item.getItemId() == R.id.menu_prefs)
       {
    		
    		appAlreadyStarted = 0;    		
    		Intent intent = new Intent()
       				.setClass(this,
       						preferences.class);   
    				//this.startActivityForResult(intent, 0);
    				this.startActivity(intent);
       }
       return true;
    }
    
    


@Override
    public void onActivityResult(int reqCode, int resCode, Intent data) //we'll go into a reset after this
    {
    	super.onActivityResult(reqCode, resCode, data);    	
    	setPreferences(); //very important to have this here, after the menu comes back this is called, we'll want to apply the new prefs without having to re-start the app
    
    	if (resCode == WENT_TO_PREFERENCES)  {
    		setPreferences(); //very important to have this here, after the menu comes back this is called, we'll want to apply the new prefs without having to re-start the app
    		//showToast("returned from preferences");
    	}	
    	
    } 
    
    private void setPreferences() //here is where we read the shared preferences into variables
    {
     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);   
     
     noSleep = prefs.getBoolean("pref_noSleep", false);
     debug_ = prefs.getBoolean("pref_debugMode", false);
     
     scrollingKeyFrames_ = Integer.valueOf(prefs.getString(   //how smooth the scrolling, essentially the keyframes
 	        resources.getString(R.string.scrollingKeyFrames),
 	        resources.getString(R.string.scrollingKeyFramesDefault))); 
     
     
     matrix_model = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
    	        resources.getString(R.string.selected_matrix),
    	        resources.getString(R.string.matrix_default_value))); 
     
     twitterSearchString = prefs.getString(   
 	        resources.getString(R.string.pref_twitterSearchString),
 	        resources.getString(R.string.twitterSearchStringDefault)); 
     
     twitterMode = prefs.getBoolean("pref_twitterMode", false);
     
     filterTweets = prefs.getBoolean("pref_twitterFilter", true);
     
     twitterInterval = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
 	        resources.getString(R.string.twitterRefresh),
 	        resources.getString(R.string.twitterRefreshDefault))); 
     
     if (AutoSelectPanel_ && pixelHardwareID.substring(0,4).equals("PIXL") && !pixelFirmware.substring(4,5).equals("0")) { // PIXL0008 or PIXL0009 is the normal so if it's just a 0 for the 5th character, then we don't go here
	    	
 	 	//let's first check if we have a matching firmware to auto-select and if not, we'll just go what the matrix from preferences
	  
	  		if (pixelHardwareID.substring(4,5).equals("Q")) {
 	 		matrix_model = 11;
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32;
		    	BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	frame_length = 2048;
		    	currentResolution = 32; 
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("T")) {
 	 		matrix_model = 14;
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x64;
		    	BitmapInputStream = getResources().openRawResource(R.raw.select64by64);
		    	frame_length = 8192;
		    	currentResolution = 128; 
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("I")) {
 	 		matrix_model = 1; 
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
		    	BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
		    	frame_length = 1024;
		    	currentResolution = 16;
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("L")) { //low power
 	 		matrix_model = 1; 
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
		    	BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
		    	frame_length = 1024;
		    	currentResolution = 16;
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("C")) {
 	 		matrix_model = 12; 
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_ColorSwap;
		    	BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	frame_length = 2048;
		    	currentResolution = 32; 
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("R")) {
 	 		matrix_model = 13; 
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32;
		    	BitmapInputStream = getResources().openRawResource(R.raw.select64by32);
		    	frame_length = 4096;
		    	currentResolution = 64; 
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("M")) { //low power
 	 		 matrix_model = 3;
 	 		 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //pixel v2
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32;
 	 	}
 	 	else if (pixelHardwareID.substring(4,5).equals("N")) { //low power
 	 		 matrix_model = 11;
 	 		 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32; //pixel v2.5
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32; 
 	 	}
 	 	else {  //in theory, we should never go here
 	 		KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32;
		    	BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	frame_length = 2048;
		    	currentResolution = 32; 
 	 	}
		}	

    else {
	     switch (matrix_model) {  //get this from the preferences
		     case 0:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
		    	 frame_length = 1024;
		    	 currentResolution = 16;
		    	 break;
		     case 1:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
		    	 frame_length = 1024;
		    	 currentResolution = 16;
		    	 break;
		     case 2:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //v1, this matrix was never used
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		    	 break;
		     case 3:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		    	 break;
		     case 4:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x32; 
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select64by32);
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;
		     case 5:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x64; 
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select32by64);
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;	 
		     case 6:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_2_MIRRORED; 
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select32by64);
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;	 	 
		     case 7: //this one doesn't work and we don't use it rigth now
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_4_MIRRORED;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select32by64);
		    	 frame_length = 8192; //original 8192
		    	 currentResolution = 128; //original 128
		    	 break;
		     case 8:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_128x32; //horizontal
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select128by32);
		    	 frame_length = 8192;
		    	 currentResolution = 128;  
		    	 break;	 
		     case 9:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x128; //vertical mount
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select32by128);
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 
		     case 10:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x64;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select64by64);
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;
		     case 11:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32; 
		    	 break;	 
		     case 12:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_ColorSwap;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32; 
		    	 break;	 	 
		     case 13:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select64by32);
		    	 frame_length = 4096;
		    	 currentResolution = 64; 
		    	 break;	
		     case 14:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x64;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select64by64);
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;
		     case 15:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_128x32;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select128by32);
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 	 	
		     case 16:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x128;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.select32by128);
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 
		     case 17:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x16;
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32; 
		    	 break;	 	 	
		     default:	    		 
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
		    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		     }
 	 }
      
     frame_ = new short [KIND.width * KIND.height];
	 BitmapBytes = new byte[KIND.width * KIND.height *2]; //512 * 2 = 1024 or 1024 * 2 = 2048
	 
	 loadRGB565(); //load the select pic raw565 file
 }
    
    private void updatePrefs() {
    	setPreferences();
    	stopTimers();
    	
    	//now we need to start twitter and scrolling text timers here
    	
    	if (scrollingtextTimer_ != null) {
    		 	resetScrolling();
    	}
    	
    	if (isNetworkAvailable()) {
	    	if (twitterMode) {
		    	twitterTimer_ = new twitterTimer (100000,twitterInterval*1000);  //the prefs is in seconds so we'll need to convert to milliseconds
		    	twitterTimer_.start();
	    	}
    	}	
    }
    
    protected void onResume() {
        super.onResume();
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        updatePrefs();
        //setPreferences();
     
    }
    
    private  void scrollTextButtonWrite() { //this gets called if the user hit the write button
    	
    	if (deviceFound == 1) {
    		scrollText(true);
    	}
    	else {
    		showToast("PIXEL was not found, did you Bluetooth pair to PIXEL?");
    	}
    }
    
	private void scrollText(boolean writeMode) 
	 
	    {
		   if(scrollingtextTimer_ != null)
			   scrollingtextTimer_.cancel();
		
		 // stopExistingTimer(); //
			
			if (pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //in write mode, we don't need a timer because we don't need a delay in between frames, we will first put PIXEL in write mode and then send all frames at once
					pixel.interactiveMode();
					float textFPS = 1000.f / scrollSpeedValue;  //TO DO need to do the math so the scrollig speed is right, need to change this formula
					
					//if (KIND.width == 64)  {  //TO DO this is a hack, we have a super pixel and need to put the max FPS, pixel's firmware will automatically go max fps when the frame rate is too high like 500 fps, fix this hack later
						//pixel.writeMode(500);
					//}
					//else {
						pixel.writeMode(textFPS);
					//}
						
					stopTimers(); //stop the twitter timer if it's running	
					
					writePixelAsync loadApplication = new writePixelAsync();
	    			loadApplication.execute();
			}
			else {   //we're not writing so let's just stream
		 
				
	         //   stopExistingTimer(); //is this needed, probably no
	    				   
	    				 //  ActionListener ScrollingTextTimer = new ActionListener() {

	    	               //     public void actionPerformed(ActionEvent actionEvent) {
				if(scrollingtextTimer_ != null)
					   scrollingtextTimer_.cancel();
						 		
				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						
						paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
	 	                scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
	 	            	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
	 	            	yCenter = (KIND.height / 2) + ((bounds.height())/2 + yOffset);
						scrollingtextTimer_ = new ScrollingTextTimer (100000,scrollSpeedValue);
						//scrollingtextTimer_ = new ScrollingTextTimer (100000,1);
				 		scrollingtextTimer_.start();
					}
				});
				
				
	    	  
	    	}    
	    }
    
	 
	 
	 private static void loadRGB565() {
	 	   
		try {
   			int n = BitmapInputStream.read(BitmapBytes, 0, BitmapBytes.length); // reads
   																				// the
   																				// input
   																				// stream
   																				// into
   																				// a
   																				// byte
   																				// array
   			Arrays.fill(BitmapBytes, n, BitmapBytes.length, (byte) 0);
   		} catch (IOException e) {
   			e.printStackTrace();
   		}

   		int y = 0;
   		for (int i = 0; i < frame_.length; i++) {
   			frame_[i] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
   			y = y + 2;
   		}
   }
    
	
	 public class writePixelAsync extends AsyncTask<Void, Integer, Void>{
			
		 int progress_status;
		 //private int y;
	      
		  @Override
		  protected void onPreExecute() {
	      super.onPreExecute();
	      
	  	paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
        scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
      	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
      	yCenter = (KIND.height / 2) + ((bounds.height())/2 + yOffset);
		
		messageWidth = bounds.width();        
	        System.out.println("message width in write mode" + " " + messageWidth);
	        //x = 0;
	        x = KIND.width *2 ; //need to have this to have the space
	        resetX = 0 - messageWidth;
	        System.out.println("ResetX" + " " + resetX);
	        System.out.println("x" + " " + x);
	    
	     progress = new ProgressDialog(ScrollingTextActivity.this);
		        progress.setMax(Math.abs(resetX));
		        progress.setTitle("Writing to PIXEL, please do not interrupt or leave this screen");
		        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		        progress.setCancelable(false); //must have this as we don't want users cancel while it's writing
		        progress.show();
	  }
	      
	  @Override
	  protected Void doInBackground(Void... params) {
			
			while (x >= resetX) {
			
				try 
  	            {	
  	               
					pixel.writeMessageToPixel(x, scrollingText, paint,yCenter); //let's write the text
  	               // x = x - scrollingKeyFrames_ ;  //this wasn't getting the latest so changed to below
  	                x = x - (scrollSpeedSeekBar_.getProgress() + 1);
					System.out.println("writing frame" + " " + x);
					progress_status++;
				    publishProgress(progress_status);
  	                
  	            } 
  	            catch (ConnectionLostException ex) 
  	            {
  	               
  	            }
		}
			 
	   return null;
	  }
	  
	  @Override
	  protected void onProgressUpdate(Integer... values) {
	   super.onProgressUpdate(values);
	   
	   progress.incrementProgressBy(1*(scrollSpeedSeekBar_.getProgress() + 1));
	    
	  }
	   
	  @Override
	  protected void onPostExecute(Void result) {
		  progress.dismiss();
		  pixel.playLocalMode();
		  
		  //ok we're done writing so let's restart the twitter timer if we need to
		  if (isNetworkAvailable()) {
		    	if (twitterMode) {
			    	twitterTimer_ = new twitterTimer (100000,twitterInterval*1000);  //the prefs is in seconds so we'll need to convert to milliseconds
			    	twitterTimer_.start();
		    	}
	    	}	
		  
		  
	  
	  super.onPostExecute(result);
}
	
}
	 
	 private class IOIOThread extends BaseIOIOLooper {
			
		//private RgbLedMatrix ledMatrix;
		
		//private Pixel pixel;

		@Override
		public void setup() throws ConnectionLostException 
		{
			try 
			{
				matrix_ = ioio_.openRgbLedMatrix(KIND);
				deviceFound = 1; //set this flag so the pop up doesn't come
				
				//**** let's get IOIO version info for the About Screen ****
	  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
	  			pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
	  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER); 
	  			IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
	  			//**********************************************************
				
	  			if (!pixelHardwareID.substring(0,4).equals("PIXL"))  //don't show the write button if it's not a PIXEL V2 board
	  			    hideWriteButton(); //have to do this as runnable or we'll get a crash
				
	  			 if (AutoSelectPanel_ && pixelHardwareID.substring(0,4).equals("PIXL") && !pixelHardwareID.substring(4,5).equals("0")) { //only go here if we have a firmware that is set to auto-detect, otherwise we can skip this
	 	  			runOnUiThread(new Runnable() 
	 	  			{
	 	  			   public void run() 
	 	  			   {
	 	  				  
	 	  				   updatePrefs();
	 	  				   
	 	  				   pixel = new Pixel(matrix_, KIND);
						 //matrix_ = ioio_.openRgbLedMatrix(KIND);
	 	  			      
	 	  			   }
	 	  			}); 
	   			}
	   		   
	   		   else { //we didn't auto-detect so just go the normal way
	   			  //matrix_ = ioio_.openRgbLedMatrix(KIND);
	   			  pixel = new Pixel(matrix_, KIND);
	   		   }
	  			
	  			
	  			//pixel = new Pixel(matrix_, KIND);
				System.out.println("PIXEL found, Hardware ID: " + pixelHardwareID);
				
				enableUi(true);
				scrollText(false); //start scrolling text, false means we stream and not write. User can write if they press write buttonl start scrolling when the app starts
			
			} 
			catch (ConnectionLostException e) 
			{
				enableUi(false);
				throw e;
			}
		}
	}

	@Override
  	protected IOIOLooper createIOIOLooper() {
  		return new IOIOThread();
  	}

	private void enableUi(final boolean enable) 
	{
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				scrollSpeedSeekBar_.setEnabled(enable);
				writeButton_.setEnabled(enable);
			}
		});
	}
	
	private void hideWriteButton() 
	{
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				writeButton_.setVisibility(View.GONE);
			}
		});
	}
	
	 public class ConnectTimer extends CountDownTimer
 	{

 		public ConnectTimer(long startTime, long interval)
 			{
 				super(startTime, interval);
 			}

 		@Override
 		public void onFinish()
 			{
 				if (deviceFound == 0) {
 					showNotFound(); 					
 				}
 				
 			}

 		@Override
 		public void onTick(long millisUntilFinished)				{
 			//not used
 		}
 	}
	 
	 public class ScrollingTextTimer extends CountDownTimer
	 	{

	 		public ScrollingTextTimer(long startTime, long interval)
	 			{
	 				super(startTime, interval); 
	 				
	 			}

	 		@Override
	 		public void onFinish()
	 			{
	 			scrollingtextTimer_.start(); //restart the timer to keep is going
	 				
	 			}

	 		@Override
	 		public void onTick(long millisUntilFinished)  {
 	            	
            	/*paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
                scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
            	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
            	int y = (KIND.height / 2) + ((bounds.height())/2);*/
            	
                try {
					pixel.writeMessageToPixel(x, scrollingText, paint, yCenter);
				} catch (ConnectionLostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //let's write the text
 	                        
 	            messageWidth = bounds.width(); 
 	            
 	           /* System.out.println("resetX is:" + " " + resetX);
 	            System.out.println("x is:" + " " + x);*/
 	            
 	            resetX = 0 - messageWidth;
 	            
 	            if(x < resetX)
 	            {
 	                x = KIND.width *2;
 	            }
 	            else
 	            {
 	                //x = x - scrollingKeyFrames_ ;  
 	                x = x - (scrollSpeedSeekBar_.getProgress() + 1);
 	            }
	 		}
	 	}
	 
	 public class twitterTimer extends CountDownTimer
	 	{

	 		public twitterTimer(long startTime, long interval)
	 			{
	 				super(startTime, interval); 
	 				
	 			}

	 		@Override
	 		public void onFinish()
	 			{
	 			twitterTimer_.start(); //restart the timer to keep is going
	 				
	 			}

	 		@Override
	 		public void onTick(long millisUntilFinished)  {

	 			twitterSearchAsync loadApplication = new twitterSearchAsync(); //twitter has to run as an async task on android or we'll get an error
    			loadApplication.execute();
	 			
	 		}
	 	}	 
	 
  
  private void showNotFound() {	
		AlertDialog.Builder alert=new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.notFoundString)).setIcon(R.drawable.icon).setMessage(getResources().getString(R.string.bluetoothPairingString)).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
  }
	
	private void setText(final String str) 
	{
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				textView_.setText(str);
			}
		});
	}
	
	 public class twitterSearchAsync extends AsyncTask<Void, Integer, Void>{
	      
		  @Override
		  protected void onPreExecute() {
	      super.onPreExecute();
	     
	  }
	      
	  @Override
	  protected Void doInBackground(Void... params) {
		  
		   	query = new Query(twitterSearchString);
		        
				try {
					result = twitter.search(query);
					System.out.println("Number of matched tweets: " + result.getCount());
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (twitter4j.Status status : result.getTweets()) {
					
					if (filterTweets) { // then we don't want @ mentions or http:// tweets
						if (!status.getText().contains("RT") && !status.getText().contains("http://") && !status.getText().contains("@")) {   //retweets have "RT" in them, we don't want retweets in this case
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							twitterResult = status.getText();
							System.out.println(status.getText());
							
						}
					}
					
					else {
						if (!status.getText().contains("RT")) {
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							twitterResult = status.getText();
							System.out.println(status.getText()); //it's the last one so let's display it
						}
					}
		        }
				
				if (twitterResult == null) {
					twitterResult = "No match found for Twitter search: " + twitterSearchString;
				}
				
				
			return null;
	  
	  }
	  
	  @Override
	  protected void onProgressUpdate(Integer... values) {
	   super.onProgressUpdate(values);
	    
	  }
	   
	  @Override
	  protected void onPostExecute(Void result) {
	
			textField.setText(twitterResult);
	  
	  super.onPostExecute(result);
}
	
}
	
	
	
	
	
	
	
	
}
