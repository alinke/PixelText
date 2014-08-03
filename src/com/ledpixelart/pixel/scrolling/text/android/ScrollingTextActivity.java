
package com.ledpixelart.pixel.scrolling.text.android;


import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import alt.android.os.CountDownTimer;
import android.annotation.SuppressLint;
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
	private static int scrollSpeedValue = 1;
	private int fontSizeValue = 26;
	
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private Button writeButton_;
	private TextView text;
	private static int ColorWheel;
	private static Paint paint;
	private Typeface tf;
	private static String scrollingText; //used for scrolling text
	private static Rect bounds;
	private static int resetX;
	private static int messageWidth;
	private static int x;	
	private int stepSize = 6;
	private String prefFontSize;
	private int prefColor;
	private String prefScrollSpeed;
	private String prefScrollingText;
	
	private Spinner fontSpinner;
	
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
    private RgbLedMatrix ledMatrix;
    private static int scrollingKeyFrames_ = 1;
	private static final int REQUEST_PAIR_DEVICE = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        
    	//TO DO this app should accept text sharing from other android apps
    	
    	//after ioio setup, we'll call the method with the timer to scroll the text, this timer will just keep running until we stop it
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

     //   scrollSpeedtextView_ = (TextView)findViewById(R.id.scrollSpeedtextView);
        scrollSpeedSeekBar_ = (SeekBar)findViewById(R.id.SeekBar);
        scrollSpeedSeekBar_.setOnSeekBarChangeListener(OnSeekBarProgress);
        //set the maximum of seekbars as 100%
        scrollSpeedSeekBar_.setMax(10);
        
        fontSizeSeekBar_ = (SeekBar)findViewById(R.id.FontSeekBar);
        fontSizeSeekBar_.setOnSeekBarChangeListener(OnSeekBarProgress);
        
        //toggleButton_ = (ToggleButton)findViewById(R.id.ToggleButton);  //not used
        
        textField = (EditText) findViewById(R.id.textField);  //the scrolling text
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
    	prefs = getSharedPreferences("appSave", MODE_PRIVATE);
        prefFontSize = prefs.getString("fontKey", "14");
        prefScrollSpeed = prefs.getString("scrollSpeedKey", "8");
        prefScrollingText = prefs.getString("scrollingTextKey","Type Text Here");
        prefColor = prefs.getInt("colorKey", 333333);
        prefFontPosition = prefs.getInt("fontPositionKey", 0);
       // showToast("font size: " + prefColor);
        
        textField.setText(prefScrollingText);
        
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
        
        picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		button = (Button) findViewById(R.id.button1);
		writeButton_ = (Button) findViewById(R.id.writeButton);
		text = (TextView) findViewById(R.id.textView1);
		
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);
		
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
      
       // scrollSpeedSeekBar_.setProgress(scrollSpeed);
        scrollSpeedSeekBar_.setProgress(Integer.parseInt(prefScrollSpeed.toString()));     
       // scrollSpeedValue = scrollSpeed;
        scrollSpeedValue = Integer.parseInt(prefScrollSpeed.toString());
        fontSizeSeekBar_.setProgress(Integer.parseInt(prefFontSize.toString()));
        
       // prefFontSize
        
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
	
	        int index = arg0.getSelectedItemPosition();
	
	        // storing string resources into Array
	        fontlist = getResources().getStringArray(R.array.font_options);
	        
	       //setFont is a function below in the code which sets the font based on the position passed
	        setFont(index);
	       // x=64; //resetting the spacing
	        x=KIND.width *2 ;
	        
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
        prefFontSizeNum = ((int)Math.round(prefFontSizeNum/stepSize))*stepSize + 16;
		paint.setTextSize(prefFontSizeNum);
    	paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    	
    	
    	textField.addTextChangedListener(new TextWatcher(){  //had to add this , without it the text will disappear sometimes when charcters are removed, x becomes higher than the message length
            public void afterTextChanged(Editable s) {
            	x = 64;
            	//now let's save the user's entered scrolling text so the next time the app comes up, they don't have to re-type
            	mEditor = prefs.edit();
                mEditor.putString("scrollingTextKey", textField.getText().toString());
                mEditor.commit();
            	
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
    	
    	
    	
    }  //end oncreate

    OnSeekBarChangeListener OnSeekBarProgress =
        	new OnSeekBarChangeListener() {

        	public void onProgressChanged(SeekBar s, int progress, boolean touch){
        	
	            if(touch){
	        	
	            	if(s == scrollSpeedSeekBar_){
		            	scrollSpeedValue = progress;
		        		//scrollSpeedtextView_.setText(Integer.toString(progress));
		        		
		        		mEditor = prefs.edit();
		                mEditor.putString("scrollSpeedKey", String.valueOf(scrollSpeedValue));
		                mEditor.commit();
		        		
	            	}
	            	    
	            	if(s == fontSizeSeekBar_) {
	            		int rawProgress = progress;
	            		progress = ((int)Math.round(progress/stepSize))*stepSize + 16;
	            		fontSizeValue = progress;
	            		paint.setTextSize(fontSizeValue);
	            	    mEditor = prefs.edit();
	            		mEditor.putString("fontKey", String.valueOf(rawProgress));
	            		mEditor.commit();
	            	    //showToast("font size: " + String.valueOf(fontSizeValue));
	            		//x=64;
	            		x=KIND.width *2 ;
		            }
	            	
	            	
	            }
      }
        	
	
    
        	public void onStartTrackingTouch(SeekBar s){

        	}

        	public void onStopTrackingTouch(SeekBar s){

        	}
    };
    
    @SuppressLint("ParserError")
	private void handleSendText(Intent intent) {  //not used
	    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	    if (sharedText != null) {
	    	textField.setText(sharedText);
	    }
    }
    
    public void onColorChanged(int color) {
    	ColorWheel = color;
    	//let's save the last color picked so the user doesn't have to re-enter next time they run the app
    	mEditor = prefs.edit();
 		mEditor.putInt("colorKey", ColorWheel);
 		mEditor.commit();
    	
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
    
    } 
    
    private void setPreferences() //here is where we read the shared preferences into variables
    {
     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);   
     
     noSleep = prefs.getBoolean("pref_noSleep", false);
     debug_ = prefs.getBoolean("pref_debugMode", false);
     
     //scrollSpeed = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
 	   //     resources.getString(R.string.selected_scrollSpeed),
 	    //    resources.getString(R.string.scrollSpeed_default_value))); 
     
     scrollingKeyFrames_ = Integer.valueOf(prefs.getString(   //how smooth the scrolling, essentially the keyframes
 	        resources.getString(R.string.scrollingKeyFrames),
 	        resources.getString(R.string.scrollingKeyFramesDefault))); 
     
     
     matrix_model = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
    	        resources.getString(R.string.selected_matrix),
    	        resources.getString(R.string.matrix_default_value))); 
     
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
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //v1
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
     case 7:
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_4_MIRRORED;
    	 BitmapInputStream = getResources().openRawResource(R.raw.select32by128);
    	 frame_length = 8192;
    	 currentResolution = 128; 
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
     default:	    		 
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
    	 frame_length = 2048;
    	 currentResolution = 32;
     }
     
     //matrix_number = matrix_model;
         
     frame_ = new short [KIND.width * KIND.height];
	 BitmapBytes = new byte[KIND.width * KIND.height *2]; //512 * 2 = 1024 or 1024 * 2 = 2048
	 
	 loadRGB565(); //this function loads a raw RGB565 image to the matrix
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
					pixel.writeMode(textFPS); //need to tell PIXEL the frames per second to use, how fast to play the animations
					showToast("Writing message, PIXEL will go blank until writing is done...");
					System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
					System.out.println("Sorry, writing scrolling text is not yet supported..."); 
					/*  int y;
				    	 
				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
				      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
				 		
				 			//framestring = "animations/decoded/" + animation_name + ".rgb565";
				 			//System.out.println("Writing to PIXEL: Frame " + y + "of " + GIFnumFrames + " Total Frames");

			    			System.out.println("Writing " + gifFileName_ + " to PIXEL " + "frame " + y);
				 		    pixel.SendPixelDecodedFrame(currentDir, gifFileName_, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
				   	  } //end for loop
*/					//pixel.playLocalMode(); //now tell PIXEL to play locally
					//System.out.println("Writing " + gifFileName_ + " to PIXEL complete, now displaying...");
					
					
					paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
  	                scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
  	            	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
					
					messageWidth = bounds.width();        
	      	        System.out.println("message width in write mode" + " " + messageWidth);
	      	        resetX = 0 - messageWidth;
					
					while (x <= resetX) {
					
						try 
	      	            {	            	
	      	            	
	      	            	//paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
	      	                //scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
	      	            	//paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
	      	                pixel.writeMessageToPixel(x, scrollingText, paint); //let's write the text
	      	                
	      	            } 
	      	            catch (ConnectionLostException ex) 
	      	            {
	      	               // Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	      	            }
						
						 x = x - scrollingKeyFrames_ ;  //controls the smoothness / number of keyframes
	      	                        
	      	           /* messageWidth = bounds.width();        
	      	           // System.out.println("message width" + " " + messageWidth);
	      	            
	      	            resetX = 0 - messageWidth;
	      	            
	      	            if(x == resetX)
	      	            {
	      	               // x = 64; //was this when hard coded for 32x32
	      	                x = KIND.width *2;
	      	            }
	      	            else
	      	            {
	      	                //x--;
	      	                x = x - scrollingKeyFrames_ ;  //let's add a new pref here
	      	            }*/
				}
					
					pixel.playLocalMode(); //we're done writing the message so now tell PIXEL to play locally	
					
			}
			else {   //we're not writing so let's just stream
		 
				
	         //   stopExistingTimer(); //is this needed, probably no
	    				   
	    				 //  ActionListener ScrollingTextTimer = new ActionListener() {

	    	               //     public void actionPerformed(ActionEvent actionEvent) {
				if(scrollingtextTimer_ != null)
					   scrollingtextTimer_.cancel();
						 		
		 		scrollingtextTimer_ = new ScrollingTextTimer (100000,scrollSpeedValue);
		 		scrollingtextTimer_.start();
	    	                    
	    	                    	
                /*	 try 
     	            {	            	
     	            	
     	            	paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
     	                scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
     	            	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
     	                pixel.writeImagetoMatrix(x, scrollingText, paint); //let's write the text
     	                
     	            } 
     	            catch (ConnectionLostException ex) 
     	            {
     	               
     	            }
     	            
     	            try 
     	            {
     					
     	            	Thread.sleep(90 - (scrollSpeedValue*10));  //the max is 90
     				} 
     	            catch (InterruptedException e) 
     				{
     					//System.out.println("coudl not sleep in " + getClass().getName() );
     				}
     	                        
     	            messageWidth = bounds.width();        
     	           // System.out.println("message width" + " " + messageWidth);
     	            
     	            resetX = 0 - messageWidth;
     	            
     	            if(x == resetX)
     	            {
     	               // x = 64; //was this when hard coded for 32x32
     	                x = KIND.width *2;
     	            }
     	            else
     	            {
     	                //x--;
     	                x = x - scrollingKeyFrames_ ;  //let's add a new pref here
     	            }
	    	         	         //   System.out.println("resetX: " + resetX);
	    	         	        //    System.out.println("x: " + x);
	    	                    	
	    	                    	
	    	                    	//delay = 5;	
	    	                    	//scrollingTextDelay_ = 710 - scrollingTextDelay_;                            // al linke: added this so the higher slider value means faster scrolling
	    	                   	    
	    	                   	  //  ScrollingTextPanel.this.timer.setDelay(delay);
	    	                    	
	    	                    	
	    	                   	    
	    	                               int w = 64 * KIND.width/32;  //originally this was w = 64 and h = 64 hard coded for the 32x32 matrix
	    	                               int h = 64 *  KIND.height/32;
	    	                    	
	    	                    			int w = KIND.width * 2;
	    	                    			int h = KIND.height* 2;
	    	                   	    
	    	                               BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    	                               
	    	                               //let's set the text color
	    	                               if (scrollingTextColor_.equals("red")) {
	    	                            	   textColor = Color.RED;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("green")) {
	    	                            	   textColor = Color.GREEN;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("blue")) {
	    	                            	   textColor = Color.BLUE;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("cyan")) {
	    	                            	   textColor = Color.CYAN;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("gray")) {
	    	                            	   textColor = Color.GRAY;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("magenta") || scrollingTextColor_.equals("purple")) {
	    	                            	   textColor = Color.MAGENTA;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("orange")) {
	    	                            	   textColor = Color.ORANGE;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("pink")) {
	    	                            	   textColor = Color.PINK;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("yellow")) {
	    	                            	   textColor = Color.YELLOW;
	    	                               }
	    	                               
	    	                              // Color textColor = colorPanel.getBackground();
	    	                               //Color myColor = new Color (246, 27, 27)
	    	                   	    
	    	                               Graphics2D g2d = img.createGraphics();
	    	                               g2d.setPaint(textColor);
	    	                               
	    	                              // Font tr = new Font("TimesRoman", Font.PLAIN, scrollingTextFontSize_);
	    	                               Font tr = new Font("Arial", Font.PLAIN, scrollingTextFontSize_);
	    	                              // Font trb = new Font("TimesRoman", Font.BOLD, scrollingTextFontSize_);
	    	                              // Font tri = new Font("TimesRoman", Font.ITALIC, scrollingTextFontSize_);
	    	                               
	    	                               //String fontFamily = fontFamilyChooser.getSelectedItem().toString();
	    	                               
	    	                               Font font = fonts.get(fontFamily);
	    	                               if(font == null)
	    	                               {
	    	                                   font = new Font(fontFamily, Font.PLAIN, 32);
	    	                                   fonts.put(fontFamily, font);
	    	                               }            
	    	                               
	    	                               g2d.setFont(tr);
	    	                               
	    	                              String message = scrollingText;
	    	                               //String message = "hard code test";
	    	                               
	    	                               FontMetrics fm = g2d.getFontMetrics();
	    	                               
	    	                               int y = fm.getHeight();   //30 = 30 * 16/32 = 15  
	    	                               y = y * KIND.height/32;
	    	                              // System.out.println("font height: " + y);

	    	                               try 
	    	                               {
	    	                                   additionalBackgroundDrawing(g2d);
	    	                               } 
	    	                               catch (Exception ex) 
	    	                               {
	    	                                  // Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    	                               }
	    	                               
	    	                               g2d.drawString(message, x, y);
	    	                               
	    	                               try 
	    	                               {
	    	                                   additionalForegroundDrawing(g2d);
	    	                               } 
	    	                               catch (Exception ex) 
	    	                               {
	    	                                   //Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    	                               }
	    	                               
	    	                               g2d.dispose();

	    	                               if(pixel != null)
	    	                               {
	    	                                   try 
	    	                                   {  
	    	                                       pixel.writeImagetoMatrix(img,KIND.width,KIND.height);
	    	                                   } 
	    	                                   catch (ConnectionLostException ex) 
	    	                                   {
	    	                                       //Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    	                                   }                
	    	                               }
	    	                                           
	    	                               int messageWidth = fm.stringWidth(message);            
	    	                               int resetX = 0 - messageWidth;
	    	                               
	    	                               if(x == resetX)
	    	                               {
	    	                                   x = w;
	    	                               }
	    	                               else
	    	                               {
	    	                                   x--;
	    	                               }
	                    }
	                };
	    				   
	    				   
	    				   timer = new Timer(scrollSpeedValue, ScrollingTextTimer); //the timer calls this function per the interval of fps
	    				   timer.start();*/
	    	}    
	    }
    
	  /* private static void stopExistingTimer()
	    {
	        if(timer != null && timer.isRunning() )
	        {
	            //System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
	            timer.stop();
	        }        
	    }*/
	 
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
    
	
	class Looper extends BaseIOIOLooper 
	{		
		//private RgbLedMatrix ledMatrix;
		
		//private Pixel pixel;

		@Override
		public void setup() throws ConnectionLostException 
		{
			try 
			{
				
				pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER); 
				//RgbLedMatrix.Matrix type = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;  //took this out because we set the LED matrix type from prefs
				//ledMatrix = ioio_.openRgbLedMatrix(type);
				deviceFound = 1; //set this flag so the pop up doesn't come
//				Toast toast = Toast.makeText(getApplicationContext() , "matrix obtained", Toast.LENGTH_SHORT);
//				toast.show();
				
				//**** let's get IOIO version info for the About Screen ****
	  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
	  			pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
	  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER); 
	  			IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
	  			//**********************************************************
				
				if (!pixelHardwareID.substring(0,4).equals("PIXL"))  //don't show the write button if it's not a PIXEL V2 board
					writeButton_.setVisibility(View.GONE); 
				
				//pixel = new Pixel(ledMatrix, type);
				
//				toast.setText("PIXEL obtained.");
				System.out.println("PIXEL obtained");
				
				enableUi(true);
				scrollText(false); //start scrolling text, false means we stream and not write. User can write if they press write button
			} 
			catch (ConnectionLostException e) 
			{
				enableUi(false);
				throw e;
			}
		}
		
		// we can't have this in the loop as we need to go faster, the loop maxes out at 30 fps
		/*@Override
		public void loop() throws ConnectionLostException 
		{ 
			{
//				int w = 64;	            

	         //   Rect bounds = new Rect();
	            try 
	            {	            	
	            	
	            	paint.setColor(ColorWheel);
	                scrollingText = textField.getText().toString();
	            	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
	                pixel.writeImagetoMatrix(x, scrollingText, paint);
	                
	            } 
	            catch (ConnectionLostException ex) 
	            {
	                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	            }
	            
	            try 
	            {
					
	            	Thread.sleep(90 - (scrollSpeedValue*10));  //the max is 90
				} 
	            catch (InterruptedException e) 
				{
					System.out.println("coudl not sleep in " + getClass().getName() );
				}
	            
	                        
	            messageWidth = bounds.width();        
	           // System.out.println("message width" + " " + messageWidth);
	            
	            resetX = 0 - messageWidth;
	            
	            if(x == resetX)
	            {
	                x = 64;
	            }
	            else
	            {
	                x--;
	            }
	         //   System.out.println("resetX: " + resetX);
	        //    System.out.println("x: " + x);
	            
	            
			}	
		}*/
	}

	@Override
	protected IOIOLooper createIOIOLooper() 
	{
		return new Looper();
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
	 		public void onTick(long millisUntilFinished)				{
	 			try 
 	            {	            	
 	            	
 	            	paint.setColor(ColorWheel); //let's get the color the user has specified from the color wheel widget
 	                scrollingText = textField.getText().toString(); //let's get the text the user has mentioned
 	            	paint.getTextBounds(scrollingText, 0, scrollingText.length(), bounds);
 	                pixel.writeMessageToPixel(x, scrollingText, paint); //let's write the text
 	                
 	            } 
 	            catch (ConnectionLostException ex) 
 	            {
 	               // Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
 	            }
 	                        
 	            messageWidth = bounds.width(); 
 	            
 	            resetX = 0 - messageWidth;
 	            
 	            if(x == resetX)
 	            {
 	                x = KIND.width *2;
 	            }
 	            else
 	            {
 	                x = x - scrollingKeyFrames_ ;  //let's add a new pref here
 	            }
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
	
}
