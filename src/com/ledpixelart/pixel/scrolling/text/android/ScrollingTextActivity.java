
package com.ledpixelart.pixel.scrolling.text.android;


import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.colorpicker.OpacityBar;
import com.larswerkman.colorpicker.SVBar;
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
	private TextView scrollSpeedtextView_;	
	private SeekBar scrollSpeedSeekBar_;	
	private SeekBar fontSizeSeekBar_;	
	private ToggleButton toggleButton_;	
	private EditText textField;	
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
  
   // private static DecodedTimer decodedtimer; 
	private Canvas canvas;
	private static Canvas canvasIOIO;
	
	private String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    private String basepath = extStorageDirectory;
    private static Context context;
    private Context frameContext;
	private boolean debug_;
	private static int appAlreadyStarted = 0;
	//private int scrollSpeedProgress = 1;
	private int scrollSpeedValue = 1;
	private int fontSizeValue = 26;
	
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;
	private int ColorWheel;
	private Paint paint;
	private Typeface tf;
	private String scrollingText; //used for scrolling text
	private Rect bounds;
	private int resetX;
	private int messageWidth;
	private int x;	
	private int stepSize = 6;
	private String prefFontSize;
	private int prefColor;
	private String prefScrollSpeed;
	

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        scrollSpeedtextView_ = (TextView)findViewById(R.id.scrollSpeedtextView);
        scrollSpeedSeekBar_ = (SeekBar)findViewById(R.id.SeekBar);
        scrollSpeedSeekBar_.setOnSeekBarChangeListener(OnSeekBarProgress);
        //set the maximum of seekbars as 100%
        scrollSpeedSeekBar_.setMax(10);
        
        fontSizeSeekBar_ = (SeekBar)findViewById(R.id.FontSeekBar);
        fontSizeSeekBar_.setOnSeekBarChangeListener(OnSeekBarProgress);
        
        toggleButton_ = (ToggleButton)findViewById(R.id.ToggleButton);
        
        textField = (EditText) findViewById(R.id.textField);  //the scrolling text
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
    	prefs = getSharedPreferences("appSave", MODE_PRIVATE);
        prefFontSize = prefs.getString("fontKey", "14");
        prefScrollSpeed = prefs.getString("scrollSpeedKey", "8");
        
        prefColor = prefs.getInt("colorKey", 333333);
       // showToast("font size: " + prefColor);
        
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
        //ColorWheel = Color.RED;
    	//paint.setColor(ColorWheel);
        
    	if (prefColor != 333333) {   //let's set the last color from prefs
    		ColorWheel = prefColor;
    		paint.setColor(prefColor); 
    	}
    	else {
    		ColorWheel = Color.GREEN;
        	paint.setColor(ColorWheel);
    	}
    	
    	//paint.setColor(Color.GREEN);
    	tf = Typeface.create("Helvetica",Typeface.NORMAL);   	   
    	paint.setTypeface(tf);
    	
    	//this is the intial font size setting, not this is not the progress bar setting
    	//paint.setTextSize(50);
    	
    	int prefFontSizeNum = Integer.parseInt(prefFontSize.toString());
        prefFontSizeNum = ((int)Math.round(prefFontSizeNum/stepSize))*stepSize + 16;
		paint.setTextSize(prefFontSizeNum);
    	
    	
    	paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    	
    	
    	textField.addTextChangedListener(new TextWatcher(){  //had to add this , without it the text will disappear sometimes when charcters are removed, x becomes higher than the message length
            public void afterTextChanged(Editable s) {
            	x = 64;
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
		        		scrollSpeedtextView_.setText(Integer.toString(progress));
		        		
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
	            		x=64;
		            }
	            	
	            	
	            }
      }
        	
	
    
        	public void onStartTrackingTouch(SeekBar s){

        	}

        	public void onStopTrackingTouch(SeekBar s){

        	}
    };
    
    public void onColorChanged(int color) {
    	ColorWheel = color;
    	//let's save the last color picked so the user doesn't have to re-enter next time they run the app
    	mEditor = prefs.edit();
 	//	mEditor.putString("colorKey", String.valueOf(color));
 		mEditor.putInt("colorKey", ColorWheel);
 		mEditor.commit();
    	
    	//showToast(String.valueOf(ColorWheel));
		//gives the color when it's changed.
	}

	 private  void showToast(final String msg) {
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
    	
	  if (item.getItemId() == R.id.menu_about) {
		  
		    AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      	alert.setTitle(getString(R.string.menu_about_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.menu_about_summary) + "\n\n" + getString(R.string.versionString) + " " + app_ver).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
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
    
     //scanAllPics = prefs.getBoolean("pref_scanAll", false);
     //slideShowMode = prefs.getBoolean("pref_slideshowMode", false);
     noSleep = prefs.getBoolean("pref_noSleep", false);
     debug_ = prefs.getBoolean("pref_debugMode", false);
     
     //scrollSpeed = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
 	   //     resources.getString(R.string.selected_scrollSpeed),
 	    //    resources.getString(R.string.scrollSpeed_default_value))); 
     
          
     matrix_model = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
    	        resources.getString(R.string.selected_matrix),
    	        resources.getString(R.string.matrix_default_value))); 
     
     switch (matrix_model) {  //get this from the preferences
     case 0:
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
    	 break;
     case 1:
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
    	 break;
     case 2:
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //v1
    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
    	 break;
     case 3:
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2
    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
    	 break;
     default:	    		 
    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
    	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
     }
         
     frame_ = new short [KIND.width * KIND.height];
	 BitmapBytes = new byte[KIND.width * KIND.height *2]; //512 * 2 = 1024 or 1024 * 2 = 2048
	 
	 loadRGB565(); //this function loads a raw RGB565 image to the matrix
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
    
	
	class Looper extends BaseIOIOLooper 
	{		
		private RgbLedMatrix ledMatrix;
		
		private Pixel pixel;

		@Override
		public void setup() throws ConnectionLostException 
		{
			try 
			{
				RgbLedMatrix.Matrix type = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
				ledMatrix = ioio_.openRgbLedMatrix(type);
				deviceFound = 1; //set this flag so the pop up doesn't come
//				Toast toast = Toast.makeText(getApplicationContext() , "matrix obtained", Toast.LENGTH_SHORT);
//				toast.show();
				
				pixel = new Pixel(ledMatrix, type);
				
//				toast.setText("PIXEL obtained.");
				System.out.println("PIXEL obtained");
				
				enableUi(true);
			} 
			catch (ConnectionLostException e) 
			{
				enableUi(false);
				throw e;
			}
		}
		
		@Override
		public void loop() throws ConnectionLostException 
		{ 
			{
//				int w = 64;	            

	         //   Rect bounds = new Rect();
	            try 
	            {	            	
	            	//if (textField.getText().toString() != null) {
	            		 // scrollingText = textField.getText().toString();
	            	
	            	//Paint paint = new Paint();
	            	paint.setColor(ColorWheel);
	            //	paint.setColor(Color.GREEN);
	            	//Typeface tf = Typeface.create("Helvetica",Typeface.NORMAL);   	   
	            	//paint.setTypeface(tf);
	            	//paint.setTextSize(26);
	            	//paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	            	
	            
	               // scrollingText = textField.getText().toString();
	            	
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
					//Thread.sleep(10);
	            	Thread.sleep(90 - (scrollSpeedValue*10));  //the max is 90
	            	//Thread.sleep(1);
				} 
	            catch (InterruptedException e) 
				{
					System.out.println("coudl not sleep in " + getClass().getName() );
				}
	            
	                        
	            messageWidth = bounds.width();        
	            System.out.println("message width" + " " + messageWidth);
	            
	            resetX = 0 - messageWidth;
	            
	            if(x == resetX)
	            {
	                x = 64;
	            }
	            else
	            {
	                x--;
	            }
	            System.out.println("resetX: " + resetX);
	            System.out.println("x: " + x);
	            
	            
			}	
		}
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
				toggleButton_.setEnabled(enable);
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
